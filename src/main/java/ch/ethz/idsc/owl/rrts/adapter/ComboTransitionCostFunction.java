// code by gjoel, jph
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
  /** @param transitionCostFunctions
   * @return */
  public static TransitionCostFunction of(TransitionCostFunction... transitionCostFunctions) {
    return new ComboTransitionCostFunction(Arrays.stream(transitionCostFunctions) //
        .collect(Collectors.toMap(f -> f, f -> RealScalar.ONE)));
  }

  /***************************************************/
  private final Map<TransitionCostFunction, Scalar> map;
//  private final int influence;

  /** @param map */
  public ComboTransitionCostFunction(Map<TransitionCostFunction, Scalar> map) {
    this.map = map;
//    influence = map.keySet().stream() //
//        .mapToInt(TransitionCostFunction::influence) //
//        .max() //
//        .getAsInt();
  }

  @Override // from TransitionCostFunction
  public Scalar cost(RrtsNode rrtsNode, Transition transition) {
    return map.entrySet().stream() //
        .map(entry -> entry.getKey().cost(rrtsNode, transition).multiply(entry.getValue())) //
        .reduce(Scalar::add).get();
  }

//  @Override // from TransitionCostFunction
//  public int influence() {
//    return influence;
//  }
}
