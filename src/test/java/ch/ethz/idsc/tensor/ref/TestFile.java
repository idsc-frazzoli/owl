// code by jph
package ch.ethz.idsc.tensor.ref;

import java.io.File;

import ch.ethz.idsc.tensor.ext.HomeDirectory;
import junit.framework.Assert;

public enum TestFile {
  ;
  /** Example:
   * invoking the function inside "AnimatedGifWriterTest"
   * from a method "testColor()" results in a return value of
   * new File("/home/username/AnimatedGifWriterTest_testColor.extension")
   * 
   * @param extension
   * @return file that does not exist
   * @throws Exception if file already exists */
  public static File withExtension(String extension) {
    StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
    String className = stackTraceElement.getClassName();
    int index = className.lastIndexOf('.');
    className = 0 < index //
        ? className.substring(index + 1)
        : className;
    File file = HomeDirectory.file(className + "_" + stackTraceElement.getMethodName() + "." + extension);
    Assert.assertFalse(file.exists());
    return file;
  }
}
