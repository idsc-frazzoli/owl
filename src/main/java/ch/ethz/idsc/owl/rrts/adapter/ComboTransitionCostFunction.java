// code by gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class ComboTransitionCostFunction implements TransitionCostFunction, Serializable {
  private final Map<TransitionCostFunction, Scalar> costFunctions;
  private final int influence;

  public ComboTransitionCostFunction(TransitionCostFunction... costFunctions) {
    this(Arrays.stream(costFunctions).collect(Collectors.toMap(f -> f, f -> RealScalar.ONE)));
  }

  public ComboTransitionCostFunction(Map<TransitionCostFunction, Scalar> costFunctions) {
    this.costFunctions = costFunctions;
    influence = costFunctions.keySet().stream() //
        .mapToInt(TransitionCostFunction::influence) //
        .max() //
        .getAsInt();
  }

  @Override // from TransitionCostFunction
  public Scalar cost(RrtsNode rrtsNode, Transition transition) {
    return costFunctions.entrySet().stream() //
        .map(entry -> entry.getKey().cost(rrtsNode, transition).multiply(entry.getValue())) //
        .reduce(Scalar::add).get();
  }

  @Override // from TransitionCostFunction
  public int influence() {
    return influence;
  }
}
