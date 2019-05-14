package com.theoxao.group.vo

import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.NotBlank

class PostVO {
    @NotBlank
    var groupId: String? = null
    var content: String = ""
    var imageFiles: MutableList<MultipartFile> = arrayListOf()
}