// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.lie.se2.Se2ParametricDistance;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.TensorNorm;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Nest;

/** implementation is only an approximation of the clothoid length */
public enum PseudoClothoidDistance implements TensorMetric, TensorNorm {
  INSTANCE;
  // ---
  private static final CurveSubdivision CURVE_SUBDIVISION = //
      LaneRiesenfeldCurveSubdivision.of(Clothoid3.INSTANCE, 1);
  private static final int DEPTH = 3;

  /** @param p element in SE2 of the form {px, py, p_heading}
   * @param q element in SE2 of the form {qx, qy, q_heading}
   * @return length of geodesic between p and q when projected to R^2
   * the projection is a circle segment */
  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    Tensor tensor = Nest.of(CURVE_SUBDIVISION::string, Tensors.of(p, q), DEPTH);
    Scalar sum = RealScalar.ZERO;
    Tensor a = tensor.get(0);
    for (int index = 1; index < tensor.length(); ++index)
      sum = sum.add(Se2ParametricDistance.INSTANCE.distance(a, a = tensor.get(index)));
    return sum;
  }

  /** @param xya element in SE(2) of the form {x, y, angle}
   * @return length of clothoid from origin to given element xya */
  @Override // from TensorNorm
  public Scalar norm(Tensor xya) {
    return distance(xya.map(Scalar::zero), xya);
  }
}
