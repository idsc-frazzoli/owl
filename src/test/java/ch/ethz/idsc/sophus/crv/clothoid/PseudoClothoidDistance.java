// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import java.util.Iterator;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.hs.r2.Se2ParametricDistance;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.TensorNorm;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.red.Nest;

/** implementation is only an approximation of the clothoid length */
/* package */ enum PseudoClothoidDistance implements TensorMetric, TensorNorm {
  INSTANCE;
  // ---
  private static final CurveSubdivision CURVE_SUBDIVISION = //
      LaneRiesenfeldCurveSubdivision.of(Clothoids.INSTANCE, 1);
  private static final int DEPTH = 3;

  /** @param p element in SE2 of the form {px, py, p_heading}
   * @param q element in SE2 of the form {qx, qy, q_heading}
   * @return length of geodesic between p and q when projected to R^2
   * the projection is a circle segment */
  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    Tensor tensor = Nest.of(CURVE_SUBDIVISION::string, Unprotect.byRef(p, q), DEPTH);
    Scalar sum = RealScalar.ZERO;
    Iterator<Tensor> iterator = tensor.iterator();
    Tensor a = iterator.next();
    while (iterator.hasNext())
      sum = sum.add(Se2ParametricDistance.INSTANCE.distance(a, a = iterator.next()));
    return sum;
  }

  /** @param xya element in SE(2) of the form {x, y, angle}
   * @return length of clothoid from origin to given element xya */
  @Override // from TensorNorm
  public Scalar norm(Tensor xya) {
    return distance(xya.map(Scalar::zero), xya);
  }
}
