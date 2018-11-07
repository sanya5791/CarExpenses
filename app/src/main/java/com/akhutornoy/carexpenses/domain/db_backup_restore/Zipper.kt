package com.akhutornoy.carexpenses.domain.db_backup_restore

import com.github.ajalt.timberkt.Timber
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class Zipper {

    fun zipAll(sourceFolder: File, zipFile: File) {
        Timber.d { "zipAll(): sourceDir=${sourceFolder.absolutePath}, zipFile=${zipFile.absolutePath}" }
        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile)))
                .use { zipFiles(it, sourceFolder, "") }
    }

    private fun zipFiles(zipOut: ZipOutputStream, sourceFile: File, parentDirPath: String, packFolders: Boolean = false) {

        val data = ByteArray(2048)

        for (f in sourceFile.listFiles()) {

            if (f.isDirectory) {
                if (!packFolders) {
                    return
                }

                val entry = ZipEntry(f.name + File.separator)
                entry.time = f.lastModified()
                entry.isDirectory
                entry.size = f.length()

                Timber.i { "Adding Directory: ${f.name}"  }
                zipOut.putNextEntry(entry)

                //Call recursively to add files within this directory
                zipFiles(zipOut, f, f.name)
            } else {

                if (!f.name.contains(".zip")) { //If folder contains a file with extension ".zip", skip it
                    FileInputStream(f).use { fi ->
                        BufferedInputStream(fi).use { origin ->
                            val path = parentDirPath + File.separator + f.name
                            Timber.i { "Adding file: $path" }
                            val entry = ZipEntry(path)
                            entry.time = f.lastModified()
                            entry.isDirectory
                            entry.size = f.length()
                            zipOut.putNextEntry(entry)
                            while (true) {
                                val readBytes = origin.read(data)
                                if (readBytes == -1) {
                                    break
                                }
                                zipOut.write(data, 0, readBytes)
                            }
                        }
                    }
                } else {
                    zipOut.closeEntry()
                    zipOut.close()
                }
            }
        }
    }

    fun unzipAll(destinationFolder: File, zipFile: File) {
        ZipFile(zipFile).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    Timber.d { "unzipAll(): extract=${entry.name}" }
                    File(destinationFolder.path, entry.name).outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
    }
}