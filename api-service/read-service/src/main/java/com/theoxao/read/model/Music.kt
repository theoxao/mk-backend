package com.theoxao.read.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "music")
class Music {
    var id = 0
    var path = ""
    var name = ""
}