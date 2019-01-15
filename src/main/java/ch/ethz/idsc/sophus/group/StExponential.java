// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;

public enum StExponential implements LieExponential {
  INSTANCE;
  // ---
  @Override // from LieExponential
  public Tensor exp(Tensor uv) {
    Tensor u = uv.get(0);
    Tensor v = uv.get(1);
 
    return Tensors.of( //
        Exp.FUNCTION.apply(u.Get()), //
        (Exp.FUNCTION.apply(u.Get()).subtract(RealScalar.ONE)).dot(v).divide(u.Get()));
  }

  @Override // from LieExponential
  public Tensor log(Tensor xy) {
    Tensor x = xy.get(0);
    Tensor y = xy.get(1);
    return Tensors.of( //
        Log.FUNCTION.apply(x.Get()), //
        Log.FUNCTION.apply(x.Get()).dot(y).divide(RealScalar.ONE.subtract(x.Get())));
  }
}
