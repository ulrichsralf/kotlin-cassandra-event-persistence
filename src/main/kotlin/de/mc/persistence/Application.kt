package de.mc.persistence

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

/**
 * ralf on 14.11.17.
 */

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
class Application


fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}