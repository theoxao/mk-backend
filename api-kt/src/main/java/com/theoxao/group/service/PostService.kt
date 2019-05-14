package com.theoxao.group.service

import com.theoxao.account.Message
import com.theoxao.commons.enums.MessageEnum
import com.theoxao.commons.security.Principal
import com.theoxao.commons.web.RestResponse
import com.theoxao.group.Post
import com.theoxao.group.dto.CommentDTO
import com.theoxao.group.dto.PostDTO
import com.theoxao.group.model.Comment
import com.theoxao.group.model.Like
import org.apache.commons.lang3.StringUtils
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Assert
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import com.theoxao.commons.oss.OssService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.io.FilenameUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.multipart.MultipartFile

@Service
open class PostService(private val mongoTemplate: ReactiveMongoTemplate,
                       private val reactiveMongoOperations: ReactiveMongoOperations,
                       private val template: MongoTemplate,
                       private val ossService: OssService) {

    companion object {
        const val POST_IMAGE= "post_image"
    }

    @Value("\${oss.prefix}")
    var prefix=""

    //TODO refBookName isLiked like comment etc
    fun list(groupId: String, userId: String): Mono<RestResponse<List<PostDTO>>> {
        val posts = template.find(Query.query(Criteria.where("groupId").`is`(groupId).and("deleted").`is`(false)), Post::class.java)
        val result = posts.map {
            PostDTO.fromEntity(it)
        }
        result.forEach {
            val likes = template.find(Query.query(Criteria.where("postId").`is`(it.id)).with(Sort(Sort.Direction.DESC, "createAt")), Like::class.java)
                    .map { like ->
                        if (like.userId == userId) {
                            it.liked = true
                        }
                        return@map like.nickName
                    }
            it.likeList.addAll(likes)
            val comments = template.find(Query.query(Criteria.where("postId").`is`(it.id)).with(Sort(Sort.Direction.DESC, "createAt")), Comment::class.java)
                    .map { comment ->
                        CommentDTO.fromEntity(comment)
                    }
            it.comments.addAll(comments)
        }
        return Mono.just(RestResponse<List<PostDTO>>().ok().withData(result))
    }

    fun post(post: Post, imageFiles: MutableList<MultipartFile>): Mono<RestResponse<PostDTO>> {

        val images = arrayListOf<String>()
        if (imageFiles.isNotEmpty()) {
            imageFiles.forEach {
                val key = ObjectId()
                images.add("$prefix$POST_IMAGE/${key.toHexString()}.${FilenameUtils.getExtension(it.originalFilename)}")
                GlobalScope.launch {
                        ossService.upload(it.bytes,"${key.toHexString()}.${FilenameUtils.getExtension(it.originalFilename)}" , POST_IMAGE )
                }
            }
        }
        post.images=images
        return mongoTemplate.save(post)
                .map {
                    RestResponse<PostDTO>().ok().withData(PostDTO.fromEntity(it))
                }.defaultIfEmpty(RestResponse.error("更新失败"))
    }

    /**
     * operate  0 unlike  1 like
     */
    @Transactional
    open fun like(like: Like, operate: Int, principal: Principal): Mono<RestResponse<List<String>>> {
        val query = Query.query(Criteria.where("userId").`is`(like.userId).and("postId").`is`(like.postId))
        val exist = template.exists(query, Like::class.java)
        if (operate == 0) {
            Assert.isTrue(exist, "操作有误")
            //unlike it
            //remove message
            template.remove(query, Like::class.java)
        } else {
            Assert.isTrue(!exist, "已经点赞")
            //like it
            //add message
            val post = template.findById(ObjectId(like.postId), Post::class.java)
            val message = Message()
            message.avatarUrl = principal.avatarUrl
            message.nickName = principal.displayName
            message.refId = like.postId
            message.sender = principal.id
            message.userId = post!!.userId
            message.type = MessageEnum.LIKE.code
            val content = HashMap<String, Any?>()
            content["content"] = post.content
            content["images"] = post.images
            message.content = content
            template.save(like)
        }
        template.remove(Query.query(Criteria.where("refId").`is`(like.postId).and("sender").`is`(like.userId)), Message::class.java)
        val names = template.find(Query.query(Criteria.where("postId").`is`(like.postId)).with(Sort(Sort.Direction.DESC, "createAt")), Like::class.java)
                .map {
                    it.nickName
                }
        return Mono.just(RestResponse<List<String>>().ok().withData(names))
    }

    fun remove(operatorId: String, groupId: String, postId: String): Mono<RestResponse<Any>> {
        val update = Update()
        update.set("deleted", true)
        update.set("deleteUserId", operatorId)
        return mongoTemplate.updateFirst(Query.query(Criteria.where("_id").`is`(postId).and("groupId").`is`(groupId)), update, Post::class.java)
                .map<RestResponse<Any>> {
                    Assert.isTrue(it.modifiedCount > 0, "删除失败")
                    RestResponse.success()
                }
    }

    fun comment(comment: Comment, principal: Principal): Mono<CommentDTO> {
        return reactiveMongoOperations.inTransaction().execute { template ->
            template.save(comment).doOnNext { c ->
                val message = Message()
                message.sender = comment.posterId
                message.nickName = comment.posterName
                message.avatarUrl = principal.avatarUrl
                message.refId = comment.postId
                message.type = MessageEnum.COMMENT.code
                template.findOne(Query.query(Criteria.where("_id").`is`(comment.postId)), Post::class.java).doOnNext {
                    val content = HashMap<String, Any?>()
                    if (StringUtils.isBlank(comment.replyId)) {
                        //评论的是发言
                        message.userId = it.userId
                        content["content"] = it.content
                        content["images"] = it.images
                        content["commentId"] = c.id?.toHexString()
                        content["comment"] = c.content
                    } else {
                        message.userId = comment.replyId
                        content["comment"] = it.content
                        content["reply"] = comment.content
                        content["commentId"] = c.id?.toHexString()
                    }
                    message.content = content
                    template.save(message).subscribe()
                }.subscribe()
            }.map {
                CommentDTO.fromEntity(it)
            }
        }.toMono()
    }

}
