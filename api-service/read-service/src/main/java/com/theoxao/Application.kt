package com.theoxao

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration

@SpringBootApplication(exclude = [HibernateJpaAutoConfiguration::class, DataSourceAutoConfiguration::class, JdbcTemplateAutoConfiguration::class, DataSourceTransactionManagerAutoConfiguration::class])
open class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            System.setProperty("es.set.netty.runtime.available.processors", "false");
            SpringApplication.run(Application::class.java, *args)
        }
    }
}
