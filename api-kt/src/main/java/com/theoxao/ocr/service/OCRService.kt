package com.theoxao.ocr.service

import com.baidu.aip.ocr.AipOcr
import com.fasterxml.jackson.databind.ObjectMapper
import com.theoxao.ocr.dto.OCRDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class OCRService {

    @Value("\${ocr.appId}")
    lateinit var appId: String
    @Value("\${ocr.appKey}")
    lateinit var appKey: String
    @Value("\${ocr.appSecret}")
    lateinit var appSecret: String

    fun recognize(file: MultipartFile): String {
        val client = AipOcr(appId, appKey, appSecret)
        client.setConnectionTimeoutInMillis(10000)
        client.setSocketTimeoutInMillis(60000)
        val result = client.basicGeneral(file.bytes, HashMap<String, String>())
        val ocrdto = ObjectMapper().readValue(result.toString(2), OCRDTO::class.java)
        return ocrdto.wordResult.map {
            it.words
        }.reduce { acc, s ->
            acc + s
        }
    }


}
