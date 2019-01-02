// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.sophus.curve.BSpline4CurveSubdivision;
import ch.ethz.idsc.sophus.curve.CurveSubdivision;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Put;

enum StaticHelper {
  ;
  static Scalar MAGIC_C = RationalScalar.of(1, 6);

  static CurveSubdivision split3(GeodesicInterface geodesicInterface) {
    return BSpline4CurveSubdivision.split3(geodesicInterface, MAGIC_C);
  }

  static void ephemeralDubins(String title, Tensor init, Tensor move) {
    File dir = UserHome.file("Projects/ephemeral/src/main/resources/geometry/dubins/" + title);
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
    File dir = UserHome.file("Projects/ephemeral/src/main/resources/geometry/se2");
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
