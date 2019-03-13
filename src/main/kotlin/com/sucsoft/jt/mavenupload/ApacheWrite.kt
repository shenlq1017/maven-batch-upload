package com.sucsoft.jt.mavenupload


import org.apache.commons.io.FileUtils

import java.io.File
import java.io.IOException

/**
 * description apache写文件的方法
 *
 * @author shenlq
 * @date 2018/10/12 13:23
 */
object ApacheWrite {


    /**
     * 写入文件
     * @param path 路径
     * @param name 文件名
     * @param lines 需要写入的内容
     */
    fun writeLines(path: String, name: String, lines: List<*>) {
        var separator = File.separator
        if (File.separator.endsWith(path)) {
            separator = ""
        }
        val fullPath = path + separator + name
        writeLines(fullPath, lines)
    }

    /**
     * 写入文件
     * @param fileName 文件名
     * @param lines 数据数组
     */
    fun writeLines(fileName: String, lines: List<*>) {
        writeLines(File(fileName), lines)
    }

    /**
     * 写入文件
     * @param file 文件
     * @param lines 数据数组
     */
    fun writeLines(file: File, lines: List<*>) {
        try {
            FileUtils.writeLines(file, "UTF-8", lines)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

}
