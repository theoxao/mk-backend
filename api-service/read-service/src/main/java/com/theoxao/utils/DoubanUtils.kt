package com.theoxao.utils

import com.theoxao.read.model.Booku
import org.htmlcleaner.HtmlCleaner
import org.htmlcleaner.TagNode
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
 * Created by theo on 2018/12/17
 */
@Component
class DoubanUtils {

    @Throws(Exception::class)
    fun parse(raw: String, record: Booku) {
        val tNode: TagNode?
        try {
            tNode = HtmlCleaner().clean(raw)
        } catch (e: ClassCastException) {
            return
        }

        var nodes = tNode!!.evaluateXPath("//title")
        if (nodes.isNotEmpty()) {
            val node = nodes[0] as TagNode
            val nameCS = node.text
            if (nameCS != null && !StringUtils.isEmpty(nameCS.toString())) {
                if (StringUtils.isEmpty(record.name) || record.name!!.length > nameCS.length) {
                    record.name = nameCS.toString()
                }
            }
        }
        nodes = tNode.evaluateXPath("//author/name")
        if (nodes.isNotEmpty()) {
            val node = nodes[0] as TagNode
            val authorName = node.text.toString()
            if (!StringUtils.isEmpty(authorName)) {
                record.author = authorName
            }
        } else {
            nodes = tNode.evaluateXPath("//db:attribute[@name='author']")
            if (nodes.isNotEmpty()) {
                val node = nodes[0] as TagNode
                val author = node.text.toString()
                if (!StringUtils.isEmpty(author)) {
                    record.author = author
                }
            }
        }

        nodes = tNode.evaluateXPath("//db:attribute[@name='isbn13']")
        if (nodes.isNotEmpty()) {
            val node = nodes[0] as TagNode
            val isbn = node.text.toString()
            if (!StringUtils.isEmpty(isbn)) {
                record.isbn = isbn
            }
        }

        nodes = tNode.evaluateXPath("//db:attribute[@name='author_intro']")
        if (nodes.isNotEmpty()) {
            val node = nodes[0] as TagNode
            val authorIntro = node.text.toString()
            if (!StringUtils.isEmpty(authorIntro)) {
                record.authorIntro = authorIntro
            }
        }
        nodes = tNode.evaluateXPath("//id")
        if (nodes.isNotEmpty()) {
            val node = nodes[0] as TagNode
            val id = node.text.toString()
            if (!StringUtils.isEmpty(id)) {
                record.doubanId = id
            }
        }
        nodes = tNode.evaluateXPath("//link[@rel='image']")
        if (nodes.isNotEmpty()) {
            val node = nodes[0] as TagNode
            val image = node.attributes["href"]
            if (!StringUtils.isEmpty(image)) {
                record.image = image
            }
        }
        nodes = tNode.evaluateXPath("//db:attribute[@name='publisher']")
        if (nodes.isNotEmpty()) {
            val node = nodes[0] as TagNode
            val publisher: String = node.text.toString()
            if (!StringUtils.isEmpty(publisher)) {
                record.publisher = publisher
            }
        }
        nodes = tNode.evaluateXPath("//db:attribute[@name='pages']")
        if (nodes.isNotEmpty()) {
            val node = nodes[0] as TagNode
            val pages = node.text.toString()
            try {
                Integer.parseInt(pages)
                if (!StringUtils.isEmpty(pages)) {
                    record.page = pages
                }
            } catch (e: NumberFormatException) {
            }

        }
    }

}
