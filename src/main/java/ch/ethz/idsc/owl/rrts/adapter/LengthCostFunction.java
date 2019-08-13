// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** TransitionCostFunction that is a function in Transition::length() */
public class LengthCostFunction implements TransitionCostFunction {
  public static final TransitionCostFunction IDENTITY = new LengthCostFunction(t -> t);

  public static TransitionCostFunction create(ScalarUnaryOperator function) {
    return new LengthCostFunction(function);
  }

  private final ScalarUnaryOperator function;

  private LengthCostFunction(ScalarUnaryOperator function) {
    this.function = function;
  }

  @Override
  public Scalar cost(Transition transition) {
    return function.apply(transition.length());
  }

  @Override
  public int influence() {
    return 0;
  }
}
