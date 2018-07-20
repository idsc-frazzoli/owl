// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.util.Optional;

import ch.ethz.idsc.owl.math.planar.Cross2D;
import ch.ethz.idsc.owl.math.planar.SignedCurvature2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;

enum StaticHelper {
  ;
  static Tensor curvature(Tensor curve, Scalar scale) {
    Tensor tensor = Tensors.empty();
    for (int index = 1; index < curve.length() - 1; ++index) {
      Tensor a = curve.get(index - 1).extract(0, 2);
      Tensor b = curve.get(index + 0).extract(0, 2);
      Tensor c = curve.get(index + 1).extract(0, 2);
      Optional<Scalar> optional = SignedCurvature2D.of(a, b, c);
      if (optional.isPresent()) {
        Scalar curvature = optional.get();
        Scalar factor = curvature.multiply(scale);
        Tensor normal = Normalize.of(Cross2D.of(c.subtract(a)));
        tensor.append(b.add(normal.multiply(factor)));
      }
      // else
      // System.err.println("curve undefined");
    }
    return tensor;
  }
}
