// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.planar.LogarithmicSpiral;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;

enum LogarithmicSpiralCurve {
  ;
  public static Tensor of(Scalar a, Scalar b) {
    return Subdivide.of(0, 20, 100).map(new LogarithmicSpiral(a, b));
  }
}
