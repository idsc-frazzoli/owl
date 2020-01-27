// code by jph
package ch.ethz.idsc.owl.bot.esp;

import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.math.order.VectorLexicographic;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.Transpose;

/* package */ enum EspStateTimeRaster implements StateTimeRaster {
  INSTANCE;

  @Override // from StateTimeRaster
  public Tensor convertToKey(StateTime stateTime) {
    Tensor x = stateTime.state();
    if (ScalarQ.of(x))
      return x;
    Tensor m0 = x.extract(0, 5);
    Tensor m1 = Transpose.of(m0);
    Tensor v0 = Flatten.of(m0);
    Tensor v1 = Flatten.of(m1);
    int compare = VectorLexicographic.COMPARATOR.compare(v0, v1);
    if (0 < compare)
      return x;
    return m1.append(Reverse.of(x.get(5)));
  }
}
