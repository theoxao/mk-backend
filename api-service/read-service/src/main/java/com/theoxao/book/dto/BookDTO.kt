package com.theoxao.book.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.Getter
import lombok.Setter
import org.bson.types.ObjectId
import org.springframework.beans.BeanUtils

/**
 * Created by theo on 2018/11/7
 */
@JsonIgnoreProperties(ignoreUnknown = true)
open class BookDTO {
    var id: String? = null
    var name: String? = null
    var author: String? = null
    var introduction: String? = null
    var isbn: String? = null
    var pageCount: String? = null
    var publisher: String? = null
    var cover: String? = null
    var state: Int? = null
    var weight:Long = 0
}
