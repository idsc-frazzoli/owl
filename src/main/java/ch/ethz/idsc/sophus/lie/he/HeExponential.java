// code by jph
package ch.ethz.idsc.sophus.lie.he;

import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum HeExponential implements LieExponential {
  INSTANCE;
  // ---
  @Override // from LieExponential
  public Tensor exp(Tensor uvw) {
    Tensor u = uvw.get(0);
    Tensor v = uvw.get(1);
    Tensor w = uvw.Get(2);
    return Tensors.of( //
        u, //
        v, //
        w.add(u.dot(v).Get().multiply(RationalScalar.HALF)));
  }

  @Override // from LieExponential
  public Tensor log(Tensor xyz) {
    Tensor x = xyz.get(0);
    Tensor y = xyz.get(1);
    Tensor z = xyz.Get(2);
    return Tensors.of( //
        x, //
        y, //
        z.subtract(x.dot(y).Get().multiply(RationalScalar.HALF)));
  }
}
