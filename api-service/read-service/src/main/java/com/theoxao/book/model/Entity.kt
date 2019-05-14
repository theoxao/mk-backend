package com.theoxao.book.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "entity")
class Entity {
    var id = ""
    var url = ""
    var name = ""
    var isbn = ""
    var author = ""
    var publisher = ""
    var sourceId = ""
    var image = ""
    var bimg = ""
    var price = ""
    var authorDesc = ""
    var desc = ""
    var review = ""
    var excerpt = ""
    var catelog = ""
    var down = false
    var bid = ""
}
