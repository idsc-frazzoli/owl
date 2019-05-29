// code by gjoel
package ch.ethz.idsc.sophus.planar;

import ch.ethz.idsc.owl.math.planar.ClothoidTerminalRatios;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorMetric;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.AbsSquared;
import ch.ethz.idsc.tensor.sca.Sqrt;

public enum AnalyticClothoidDistance implements TensorMetric {
  LR1(ClothoidLR1Midpoint.INSTANCE), //
  LR3(ClothoidLR3Midpoint.INSTANCE), //
  ;
  private final MidpointInterface midpointInterface;

  private AnalyticClothoidDistance(MidpointInterface midpointInterface) {
    this.midpointInterface = midpointInterface;
  }

  /** @param p element in SE2 of the form {px, py, p_heading}
   * @param q element in SE2 of the form {qx, qy, q_heading}
   * @return length of geodesic between p and q when projected to R^2
   * the projection is a circle segment */
  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    if (p.Get(2).equals(q.Get(2))) {
      Tensor m = midpointInterface.midpoint(p, q);
      if (p.Get(2).equals(m.Get(2)))
        return Norm._2.ofVector(Extract2D.FUNCTION.apply(q.subtract(p)));
      Scalar half_dist = distance(p, m);
      return half_dist.add(half_dist);
    }
    // TODO investigate "direction"
    ClothoidTerminalRatios ratios = ClothoidTerminalRatios.of(p, q);
    Scalar half_num = q.Get(2).subtract(p.Get(2));
    Scalar num = half_num.add(half_num);
    Scalar den = AbsSquared.FUNCTION.apply(ratios.tail()).subtract(AbsSquared.FUNCTION.apply(ratios.head()));
    Scalar a = Sqrt.FUNCTION.apply(num.divide(den));
    return AbsSquared.FUNCTION.apply(a).multiply(ratios.tail().subtract(ratios.head()));
  }

  public Scalar norm(Tensor q) {
    return distance(q.map(Scalar::zero), q);
  }
}
