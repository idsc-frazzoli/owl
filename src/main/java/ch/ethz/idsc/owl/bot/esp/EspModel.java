// code by jph
package ch.ethz.idsc.owl.bot.esp;

import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;

/** English sixteen problem
 * 
 * Reference:
 * Sam Loyd/Martin Gardner
 * "Mathematische Raetsel und Spiele" */
/* package */ enum EspModel implements StateSpaceModel {
  INSTANCE;

  @Override
  public Tensor f(Tensor x, Tensor u) {
    Tensor spot = x.get(5);
    int sx = spot.Get(0).number().intValue();
    int sy = spot.Get(1).number().intValue();
    if (Scalars.nonZero(x.Get(sx, sy)))
      throw TensorRuntimeException.of(x);
    Tensor vacant = spot.add(u);
    int px = vacant.Get(0).number().intValue();
    int py = vacant.Get(1).number().intValue();
    Tensor y = x.copy();
    y.set(y.Get(px, py), sx, sy);
    y.set(RealScalar.ZERO, px, py);
    y.set(u::add, 5);
    return y;
  }
}