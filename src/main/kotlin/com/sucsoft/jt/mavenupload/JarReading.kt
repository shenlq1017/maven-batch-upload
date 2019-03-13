package com.sucsoft.jt.mavenupload

import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.File
import java.io.IOException
import java.util.*
import javax.annotation.PostConstruct


/**
 * @author shenlq
 * @description
 * @date 2019-03-11 15:18
 */
@Configuration
class JarReading {

    //仓库id
    val repositoryId = "central"
    //私服url
    var dUrl = "http://localhost:8081/artifactory/smaven"
    //本地文件路径
    var dir = "C:/work/suc/maven/repo8/**"
    //生成的批处理文件
    var outBat = "C:/work/java/pro/jt/maven-upload/src/main/deploy1.bat"

    /**
     * 读取本地资源路径并打包成一个批处理文件
     */
    @PostConstruct
    fun readIng() {
        var files:List<JarGroup> = getFileNamesByDir(dir,"pom")
        var batStrs = ""
        for (item in files) {
            var installStr = installDeploy(item.groupId,item.artifactId,item.version,item.filePath,item.packing,item.pomName)
            batStrs = batStrs+ installStr
        }
        var file = File(outBat)
        ApacheWrite.writeLines(file.path, listOf(batStrs))
    }

    /**
     * 组装成一个上传语句
     */
    fun installDeploy(groupId:String,artifactId:String,version:String,filePath:String,packing:String,pomName:String):String {
        var installStr = "mvn deploy:deploy-file -DgroupId=${groupId} -DartifactId=${artifactId} " +
                "-Dversion=${version}  "
        if (packing != "pom") {
            installStr += "-DpomFile=${pomName} "
        }
        installStr += "-Dpackaging=${packing}  -Dfile=${filePath} -Durl=${dUrl} -DrepositoryId=${repositoryId} & "
        return installStr
    }

    /**
     * 拿到所有以pattern结尾的文件
     * @param dir
     * @param pattern
     * @return
     */
    fun getFileNamesByDir(dir: String, vararg patterns: String): List<JarGroup> {
        var jarGroups = ArrayList<JarGroup>()
        val resolver = PathMatchingResourcePatternResolver()
        var i = 0
        try {
            if (dir.isEmpty()) {
                return ArrayList()
            }
            val resources = resolver.getResources("file:$dir")
            for (r in resources) {
                if (patterns != null && patterns.isNotEmpty()) {
                    for (pattern in patterns) {
                        //判定后缀
                        if (r.filename!=null && r.filename!!.endsWith(pattern)) {
                            var jarGroup = JarGroup()
                            jarGroup.pomName = r.file.path
                            jarGroup.fileName = r.filename!!.replace(".pom",".jar")
                            try {
                                var reader = MavenXpp3Reader();
                                var model = reader.read(r.inputStream);
                                jarGroup.artifactId= model.artifactId
                                if(model.parent!=null) {
                                    jarGroup.groupId = model.parent.groupId
                                    jarGroup.version = model.parent.version
                                }else {
                                    jarGroup.groupId = model.groupId
                                    jarGroup.version = model.version
                                }
                                jarGroup.packing = model.packaging
                                if("jar"==jarGroup.packing) {
                                    jarGroup.filePath = r.file.path.replace(r.filename!!, jarGroup.fileName)
                                } else {
                                    jarGroup.filePath = r.file.path
                                }
                                jarGroups.add(jarGroup)
                                i++
                            } catch (e: Exception) {
                                println(r.filename)
                                break
                            }
                            break
                        }
                    }
                } else {

                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        println(i)
        return ArrayList(jarGroups)
    }
}