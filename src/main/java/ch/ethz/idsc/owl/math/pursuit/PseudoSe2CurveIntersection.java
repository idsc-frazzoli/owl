// code by jph
package ch.ethz.idsc.owl.math.pursuit;

import ch.ethz.idsc.sophus.clt.ClothoidDistance;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** DO NOT USE THIS ON A REAL ROBOT */
public class PseudoSe2CurveIntersection extends AssistedCurveIntersection {
  private static final long serialVersionUID = 3416992505669536414L;

  /** @param radius non-negative
   * @throws Exception if given radius is negative */
  public PseudoSe2CurveIntersection(Scalar radius) {
    super(radius, Se2Geodesic.INSTANCE);
  }

  @Override // from SimpleCurveIntersection
  protected Scalar distance(Tensor tensor) {
    return ClothoidDistance.SE2_ANALYTIC.norm(tensor);
  }
}
