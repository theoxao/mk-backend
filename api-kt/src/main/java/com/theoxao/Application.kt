package com.theoxao

import com.theoxao.commons.oss.OssService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


/**
 * @author theo
 * @date 2019/4/29
 */

@ConfigurationProperties("oss")
@SpringBootApplication(exclude = [HibernateJpaAutoConfiguration::class, DataSourceAutoConfiguration::class, JdbcTemplateAutoConfiguration::class, DataSourceTransactionManagerAutoConfiguration::class])
open class Application {

    var bucketName = ""
    var endpoint = ""
    var accessId = ""
    var accessKey = ""

    @Bean
    open fun ossService(): OssService {
        return OssService(bucketName, endpoint, accessId, accessKey)
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

