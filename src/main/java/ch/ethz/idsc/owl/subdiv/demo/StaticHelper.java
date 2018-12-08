// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.owl.math.planar.SignedCurvature2D;
import ch.ethz.idsc.owl.subdiv.curve.BSpline4CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.CurveSubdivision;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Put;

enum StaticHelper {
  ;
  static Scalar MAGIC_C = RationalScalar.of(1, 6);

  static CurveSubdivision split3(GeodesicInterface geodesicInterface) {
    return BSpline4CurveSubdivision.split3(geodesicInterface, MAGIC_C);
  }

  static Tensor curvature(Tensor tensor) {
    Tensor normal = Tensors.empty();
    // TODO JPH can do better at the start and end
    if (0 < tensor.length())
      normal.append(RealScalar.ZERO);
    for (int index = 1; index < tensor.length() - 1; ++index) {
      Tensor a = tensor.get(index - 1);
      Tensor b = tensor.get(index + 0);
      Tensor c = tensor.get(index + 1);
      Optional<Scalar> optional = SignedCurvature2D.of(a, b, c);
      normal.append(optional.orElse(RealScalar.ZERO));
    }
    if (1 < tensor.length())
      normal.append(RealScalar.ZERO);
    return normal;
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
