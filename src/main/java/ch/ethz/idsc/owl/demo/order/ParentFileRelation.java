// code from http://www.java2s.com/Tutorial/Java/0180__File/Checkswhetherthechilddirectoryisasubdirectoryofthebasedirectory.htm
// adapted by jph 
package ch.ethz.idsc.owl.demo.order;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import ch.ethz.idsc.owl.math.order.BinaryRelation;

public enum ParentFileRelation implements BinaryRelation<File> {
  INSTANCE;
  // ---
  /** Checks, whether the child directory is a subdirectory of the base
   * directory.
   *
   * @param base the base directory.
   * @param child the suspected child directory.
   * @return true, if the child is a subdirectory of the base directory.
   * @throws Exception if an error occurred during the test. */
  @Override
  public boolean test(File base, File child) {
    try {
      base = base.getCanonicalFile();
      child = child.getCanonicalFile();
      while (Objects.nonNull(child)) {
        if (base.equals(child))
          return true;
        child = child.getParentFile();
      }
    } catch (IOException ioException) {
      throw new RuntimeException(ioException);
    }
    return false;
  }
}
