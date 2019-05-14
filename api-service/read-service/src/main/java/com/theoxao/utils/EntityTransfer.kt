package com.theoxao.utils

import com.theoxao.book.dto.BookDTO
import com.theoxao.read.model.Booku

class EntityTransfer {

    companion object {
        fun bookuToBookDTO(entity: Booku): BookDTO {
            val record = BookDTO()
            record.name = entity.name
            record.author = entity.author
            record.isbn = entity.isbn
            record.id = entity.id?.toHexString()
            record.publisher = entity.publisher
            record.pageCount = entity.page
            record.introduction = entity.intro
            record.cover = entity.image
            return record
        }
    }


}