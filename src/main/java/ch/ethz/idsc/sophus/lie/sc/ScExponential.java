// code by jph
package ch.ethz.idsc.sophus.lie.sc;

import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;

public enum ScExponential implements LieExponential {
  INSTANCE;
  // ---
  @Override // from LieExponential
  public Scalar exp(Tensor x) {
    return Exp.FUNCTION.apply(x.Get());
  }

  @Override // from LieExponential
  public Scalar log(Tensor g) {
    return Log.FUNCTION.apply(g.Get());
  }
}
