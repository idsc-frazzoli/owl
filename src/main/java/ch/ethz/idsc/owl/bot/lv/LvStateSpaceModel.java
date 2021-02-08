// code by jph
package ch.ethz.idsc.owl.bot.lv;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** Lotka-Volterra
 * 
 * https://en.wikipedia.org/wiki/Lotka%E2%80%93Volterra_equations */
/* package */ class LvStateSpaceModel implements StateSpaceModel, Serializable {
  /** see documentation of public constructor
   * 
   * @param f0
   * @param f1
   * @return */
  public static StateSpaceModel of(Number f0, Number f1) {
    return new LvStateSpaceModel(RealScalar.of(f0), RealScalar.of(f1));
  }

  /***************************************************/
  private final Scalar f0;
  private final Scalar f1;

  /** the stable fix-point of the system is (f1, f0)
   * 
   * @param f0 decay rate for predators
   * @param f1 growth rate for prey */
  private LvStateSpaceModel(Scalar f0, Scalar f1) {
    this.f0 = f0;
    this.f1 = f1;
  }

  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    // Mathematica
    // x0' = x0 * (x1-f0) == + x0 x1 - f0 x0 (predators)
    // x1' = x1 * (f1-x0) == - x0 x1 + f1 x1 (prey)
    // hunting u adds to decay of predators:
    // F0 = f0 + u
    Scalar F0 = f0.add(u.Get(0));
    return Tensors.of( //
        x.Get(0).multiply(x.Get(1).subtract(F0)), //
        x.Get(1).multiply(f1.subtract(x.Get(0))) //
    );
  }
}
