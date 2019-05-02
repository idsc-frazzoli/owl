// code by jph
package ch.ethz.idsc.sophus.planar;

import ch.ethz.idsc.sophus.curve.ClothoidCurve;
import ch.ethz.idsc.sophus.curve.CurveSubdivision;
import ch.ethz.idsc.sophus.curve.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorMetric;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Nest;

/** implementation is only an approximation of the clothoid length */
public enum ClothoidDistance implements TensorMetric {
  INSTANCE;
  // ---
  private static final CurveSubdivision CURVE_SUBDIVISION = //
      new LaneRiesenfeldCurveSubdivision(ClothoidCurve.INSTANCE, 1);
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

  public Scalar norm(Tensor q) {
    return distance(q.map(Scalar::zero), q);
  }
}
