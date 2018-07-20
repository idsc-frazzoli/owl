// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.planar.LogarithmicSpiral;
import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Real;

enum LogarithmicSpiralCurve {
  ;
  public static Tensor of(Scalar a, Scalar b) {
    LogarithmicSpiral logarithmicSpiral = new LogarithmicSpiral(a, b);
    Tensor path = Tensors.empty();
    for (Tensor _r : Subdivide.of(0, 20, 100)) {
      Scalar theta = _r.Get();
      Scalar z = ComplexScalar.fromPolar(logarithmicSpiral.apply(theta), theta);
      path.append(Tensors.of(Real.of(z), Imag.of(z)));
    }
    return path;
  }
}
