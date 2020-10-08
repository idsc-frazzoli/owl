// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.zip.DataFormatException;

import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.Import;

public class SerUidDemo implements Serializable {
  private static final long serialVersionUID = 2039487502893475089L;
  int asd = 3;

  public void asd() {
    // ---
  }

  public static void main(String[] args) throws ClassNotFoundException, IOException, DataFormatException {
    File file = HomeDirectory.file("nouid.object");
    boolean export = false;
    if (export) {
      SerUidDemo serUidDemo = new SerUidDemo();
      Export.object(file, serUidDemo);
      System.out.println("exported");
    } else {
      Import.object(file);
      System.out.println("imported");
    }
  }
}
