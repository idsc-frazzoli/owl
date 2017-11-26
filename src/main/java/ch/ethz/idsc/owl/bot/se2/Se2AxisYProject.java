// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.ArcTan;

public enum Se2AxisYProject {
  ;
  /** @param x == {vx, 0, rate}
   * @param p == {px, py}
   * @return time to arrival of a point on the y axis that is subject to flow x to reach p */
  public static Scalar of(Tensor x, Tensor p) {
    Scalar vx = x.Get(0);
    Scalar be = x.Get(2);
    Scalar px = p.Get(0);
    Scalar py = p.Get(1);
    if (Scalars.isZero(be))
      return px.divide(vx);
    return ArcTan.of(vx.subtract(py.multiply(be)), px.multiply(be)).divide(be);
  }
}
