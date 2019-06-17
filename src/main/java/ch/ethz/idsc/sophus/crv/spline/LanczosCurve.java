// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LanczosInterpolation;

public enum LanczosCurve {
  ;
  public static Tensor refine(Tensor points, int number) {
    Interpolation interpolation = LanczosInterpolation.of(points);
    return Subdivide.of(0, points.length() - 1, number).map(interpolation::at);
  }
}
