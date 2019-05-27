// code by gjoel
package ch.ethz.idsc.sophus.planar;

import ch.ethz.idsc.owl.math.planar.ClothoidTerminalRatios;
import ch.ethz.idsc.sophus.curve.ClothoidCurve;
import ch.ethz.idsc.sophus.curve.CurveSubdivision;
import ch.ethz.idsc.sophus.curve.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorMetric;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.AbsSquared;
import ch.ethz.idsc.tensor.sca.Sqrt;

public enum AnalyticClothoidDistance implements TensorMetric {
  INSTANCE;

  /** @param p element in SE2 of the form {px, py, p_heading}
   * @param q element in SE2 of the form {qx, qy, q_heading}
   * @return length of geodesic between p and q when projected to R^2
   * the projection is a circle segment */
  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    // TODO treat singularity
    // TODO investigate
    ClothoidTerminalRatios ratios = ClothoidTerminalRatios.of(p, q);
    Scalar num = RealScalar.of(2).multiply(q.Get(2).subtract(p.Get(2)));
    Scalar den = AbsSquared.FUNCTION.apply(ratios.tail()).subtract(AbsSquared.FUNCTION.apply(ratios.head()));
    Scalar a = Sqrt.FUNCTION.apply(num.divide(den));
    return AbsSquared.FUNCTION.apply(a).multiply(ratios.tail().subtract(ratios.head()));
  }

  public Scalar norm(Tensor q) {
    return distance(q.map(Scalar::zero), q);
  }
}
