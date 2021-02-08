// code by jph
package ch.ethz.idsc.owl.bot.ip;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

/** inverted pendulum
 * 
 * from Analysis and Synthesis of Single-Input Single-Output Control Systems, 3rd edition
 * p.13, p.53 */
/* package */ class IpStateSpaceModel implements StateSpaceModel, Serializable {
  // ---
  private final Scalar M;
  private final Scalar m;
  private final Scalar l;
  private final Scalar g;

  /** @param M mass of cart
   * @param m mass of weight on top of rod
   * @param l length of rod
   * @param g gravitational constant */
  public IpStateSpaceModel(Scalar M, Scalar m, Scalar l, Scalar g) {
    this.M = M;
    this.m = m;
    this.l = l;
    this.g = g;
  }

  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    // x == [d v a w]
    Scalar v = x.Get(1);
    Scalar a = x.Get(2);
    Scalar w = x.Get(3);
    Scalar F = u.Get(0);
    Scalar sa = Sin.of(a);
    Scalar ca = Cos.of(a);
    Scalar Mpmsasa = M.add(Times.of(m, sa, sa));
    Scalar vd = F.add(Times.of(m, l, w, w, sa)).subtract(Times.of(m, g, ca, sa)).divide(Mpmsasa);
    Scalar wd = Times.of(m.add(M), g, sa).subtract(Times.of(m, l, w, w, ca, sa)).subtract(ca.multiply(F)) //
        .divide(l.multiply(Mpmsasa));
    return Tensors.of(v, vd, w, wd);
  }
}
