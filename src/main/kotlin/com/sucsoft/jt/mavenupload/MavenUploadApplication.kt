package com.sucsoft.jt.mavenupload

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MavenUploadApplication

fun main(args: Array<String>) {
    runApplication<MavenUploadApplication>(*args)
}
