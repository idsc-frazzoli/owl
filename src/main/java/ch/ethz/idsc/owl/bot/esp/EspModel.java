// code by jph
package ch.ethz.idsc.owl.bot.esp;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.DoubleScalar;
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

  static boolean region(Tensor vacant) {
    int px = vacant.Get(0).number().intValue();
    int py = vacant.Get(1).number().intValue();
    return isField(px, py);
  }

  static boolean isField(int px, int py) {
    boolean hi = 0 <= px && px <= 2 && 0 <= py && py <= 2;
    boolean lo = 2 <= px && px <= 4 && 2 <= py && py <= 4;
    return hi || lo;
  }

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
    if (isField(px, py)) {
      Tensor y = x.copy();
      y.set(y.Get(px, py), sx, sy);
      y.set(RealScalar.ZERO, px, py);
      y.set(u::add, 5);
      return y;
    }
    return DoubleScalar.INDETERMINATE;
  }
}
