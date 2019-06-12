// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;
import java.lang.reflect.Constructor;

import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class Se2TransitionSpace<T extends Se2Transition> implements TransitionSpace, Serializable {
  private final Class<T> transition;
  private final Scalar[] vars;

  public Se2TransitionSpace(Class<T> transition, Scalar... vars) {
    this.transition = transition;
    this.vars = vars;
  }

  @Override // from TransitionSpace
  public Se2Transition connect(Tensor start, Tensor end) {
    try {
      Constructor<T> constructor;
      switch (vars.length) {
        case 0:
          constructor = transition.getConstructor(Tensor.class, Tensor.class);
          return constructor.newInstance(start, end);
        case 1:
          constructor = transition.getConstructor(Tensor.class, Tensor.class, Scalar.class);
          return constructor.newInstance(start, end, vars[0]);
        default:
          constructor = transition.getConstructor(Tensor.class, Tensor.class, Scalar[].class);
          return constructor.newInstance(start, end, vars);
        }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
