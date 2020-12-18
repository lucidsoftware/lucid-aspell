package com.lucidchart.aspell

import org.apache.commons.io.IOUtils
import java.io._
import resource._

private[aspell] object NativeLibraryLoader {

  def load(name: String) = {
    val fileName = new File(getClass.getResource(name).getPath).getName
    val tempDirectory = new File(System.getProperty("java.io.tmpdir"))
    val file = new File(tempDirectory + File.separator + fileName)
    for {
      libraryStream <- managed(getClass.getResourceAsStream(name))
      fileStream <- managed(new FileOutputStream(file))
    } {
      IOUtils.copy(libraryStream, fileStream)
    }
    System.load(file.getAbsolutePath)
  }

}
