// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.owl.subdiv.curve.BSpline4CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.CurveSubdivision;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;

enum StaticHelper {
  ;
  static Scalar MAGIC_C = RationalScalar.of(1, 6);

  static CurveSubdivision split3(GeodesicInterface geodesicInterface) {
    return BSpline4CurveSubdivision.split3(geodesicInterface, MAGIC_C);
  }
}
