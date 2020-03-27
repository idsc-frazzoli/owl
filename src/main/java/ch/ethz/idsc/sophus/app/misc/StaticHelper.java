// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Put;

enum StaticHelper {
  ;
  static void ephemeralDubins(String title, Tensor init, Tensor move) {
    File dir = HomeDirectory.file("Projects", "ephemeral", "src", "main", "resources", "geometry", "dubins", title);
    dir.mkdir();
    if (dir.isDirectory())
      try {
        Put.of(new File(dir, "init.mathematica"), init);
        Put.of(new File(dir, "move.mathematica"), move);
      } catch (IOException exception) {
        exception.printStackTrace();
      }
    else
      System.err.println("no ex");
  }

  static void ephemeralSe2(String title, Tensor control) {
    File dir = HomeDirectory.file("Projects", "ephemeral", "src", "main", "resources", "geometry", "se2");
    if (dir.isDirectory())
      try {
        Put.of(new File(dir, title + ".mathematica"), control);
      } catch (IOException exception) {
        exception.printStackTrace();
      }
    else
      System.err.println("no ex");
  }
}
