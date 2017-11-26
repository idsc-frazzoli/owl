// code by jph
package ch.ethz.idsc.owl.bot.tn;

import ch.ethz.idsc.owl.math.CoordinateWrap;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

class TnIdentityWrap implements CoordinateWrap {
  @Override
  public Tensor represent(Tensor x) {
    return x.copy();
  }

  @Override
  public Scalar distance(Tensor p, Tensor q) {
    return Norm._2.between(p, q);
  }
}
