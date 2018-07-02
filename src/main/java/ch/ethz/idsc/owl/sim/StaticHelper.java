// code by jph
package ch.ethz.idsc.owl.sim;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;

enum StaticHelper {
  ;
  static Tensor create(int resolution) {
    Tensor localPoints = Tensors.empty();
    for (Tensor _xn : Subdivide.of(0, 1, resolution - 1)) {
      double xn = _xn.Get().number().doubleValue();
      double dist = 0.6 + 1.5 * xn + xn * xn;
      for (Tensor _yn : Subdivide.of(-0.5, 0.5, resolution - 1)) {
        double y = _yn.Get().number().doubleValue();
        Tensor probe = Tensors.vector(dist, y * dist);
        localPoints.append(probe);
      }
    }
    GlobalAssert.that(localPoints.length() == resolution * resolution);
    return localPoints;
  }
}
