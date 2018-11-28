// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.util.Optional;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.owl.math.planar.SignedCurvature2D;
import ch.ethz.idsc.owl.subdiv.curve.BSpline4CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.CurveSubdivision;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

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
}
