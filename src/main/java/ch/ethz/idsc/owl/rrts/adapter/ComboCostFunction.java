// code by gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class ComboCostFunction implements TransitionCostFunction {
  private final Map<TransitionCostFunction, Scalar> costFunctions;

  public ComboCostFunction(TransitionCostFunction... costFunctions) {
    this(Arrays.stream(costFunctions).collect(Collectors.toMap(f -> f, f -> RealScalar.ONE)));
  }

  public ComboCostFunction(Map<TransitionCostFunction, Scalar> costFunctions) {
    this.costFunctions = costFunctions;
  }

  @Override
  public Scalar cost(Transition transition) {
    return costFunctions.entrySet().stream().map(e -> e.getKey().cost(transition).multiply(e.getValue())).reduce(Scalar::add).get();
  }

  @Override
  public int influence() {
    return costFunctions.keySet().stream().map(TransitionCostFunction::influence).reduce(Math::max).get();
  }
}
