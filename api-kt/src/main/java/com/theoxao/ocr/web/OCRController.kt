package com.theoxao.ocr.web

import com.theoxao.ocr.dto.OCRDTO
import com.theoxao.ocr.dto.WordResultDTO
import com.theoxao.ocr.service.OCRService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/ocr")
class OCRController(private val ocrService: OCRService) {
    @PostMapping("/url")
    fun base64(@RequestParam("file") file: MultipartFile): String {
        return ocrService.recognize(file)
    }
}