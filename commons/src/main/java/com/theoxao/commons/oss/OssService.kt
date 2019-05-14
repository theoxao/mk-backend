package com.theoxao.commons.oss

import com.aliyun.openservices.oss.OSSClient
import com.aliyun.openservices.oss.model.ObjectMetadata
import org.apache.commons.lang3.StringUtils
import java.io.ByteArrayInputStream


/**
 * @author theo
 * @date 2019/5/13
 */
class OssService() {
    lateinit var bucketName: String

    lateinit var ossClient: OSSClient

    constructor(bucketName: String, endpoint: String, accessId: String, accessKey: String) : this() {
        this.bucketName = bucketName
        this.ossClient = OSSClient(endpoint, accessId, accessKey)
    }

    fun upload(data: ByteArray, key: String, fileDir: String): String? {
        val start = System.currentTimeMillis()
        ByteArrayInputStream(data).use { `is` ->
            val metadata = ObjectMetadata()
            metadata.contentLength = data.size.toLong()
            val uploadKey = if (StringUtils.isNotBlank(fileDir)) "$fileDir/$key" else key
            val result = ossClient.putObject(this.bucketName, uploadKey, `is`, metadata)
            return uploadKey
        }

    }

}