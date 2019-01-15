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
    // Hier fehlt der Spezialfall für dlambda = 0
    return Tensors.of( //
        Exp.FUNCTION.apply(u.Get()), //
        (Exp.FUNCTION.apply(u.Get()).subtract(RealScalar.ONE)).multiply(v.Get()).divide(u.Get()));
  }

  @Override // from LieExponential
  public Tensor log(Tensor xya) {
    Tensor x = xya.get(0);
    Tensor y = xya.get(1);
    // Hier Fehlt Spezialfall für lambda = 1
    return Tensors.of( //
        Log.FUNCTION.apply(x.Get()), //
        Log.FUNCTION.apply(x.Get()).multiply(y.Get()).divide(RealScalar.ONE.subtract(x.Get())));
  }
}
// Auch hier, wie funktioniert der Mehrdimensionale Fall für bspw. das EXP? Get nimmt ja nur einen Skalar