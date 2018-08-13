// code by jph
package ch.ethz.idsc.owl.bot.tn;

import ch.ethz.idsc.owl.math.CoordinateWrap;
import ch.ethz.idsc.tensor.Tensor;

class TnIdentityWrap implements CoordinateWrap {
  @Override
  public Tensor represent(Tensor x) {
    return x.copy();
  }

  // @Override
  // public Scalar distance(Tensor p, Tensor q) {
  // return Norm._2.between(p, q);
  // }
  @Override
  public Tensor difference(Tensor p, Tensor q) {
    return q.subtract(p);
  }
}
