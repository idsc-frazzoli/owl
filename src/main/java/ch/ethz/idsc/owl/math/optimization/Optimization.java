// code by astoll
package ch.ethz.idsc.owl.math.optimization;

import java.util.List;
import java.util.function.Function;

import ch.ethz.idsc.owl.math.order.OrderComparator;

/** Creates an optimization problem with a given cost functional vector
 * which map the feasible inputs onto an objective space,
 * where they can be evaluated according to a given OrderComparator.
 *
 * @param <T> type of inputs
 * @param <E> type of objectives */
public class Optimization<T, E, R> {
  // TODO ASTOLL implement as interface
  List<T> inputs;
  List<Function<T, E>> featureFunctionVector;
  List<Function<E, R>> scoringFunctionVector;
  OrderComparator<R> orderComparator;

  public Optimization(List<T> inputs, List<Function<T, E>> featureFunctionVector, List<Function<E, R>> scoringFunctionVector,
      OrderComparator<R> orderComparator) {
    this.inputs = inputs;
    this.featureFunctionVector = featureFunctionVector;
    this.orderComparator = orderComparator;
  }

  /** Apply constraints to input set
   * @return List of feasible alternatives */
  public List<T> getFeasibleAlternatives() {
    // TODO ASTOLL
    return null;
  }

  /** Map the feasible inputs onto the objective space, e.g. f(X).
   * 
   * @return Image of feasible inputs with respect to cost functional vector */
  public List<E> inputsInObjectiveSpace() {
    // return inputs.stream().map(functionVector).collect(Collectors.toSet());
    // TODO ASTOLL
    return null;
  }

  /** Gets the arguments, i.e the optimal solutions,
   * for the optimization problem <t>min f(X)</t>.
   * 
   * @return Set of optimal solutions of the OP */
  public List<T> getOptimalSolutions() {
    // TODO ASTOLL
    return null;
  }

  /** Gets the arguments, i.e the optimal solutions,
   * for the optimization problem <t>min f(X)</t>.
   * 
   * @return Set of optimal values of the OP */
  public List<R> getOptimalValues() {
    // TODO ASTOLL
    return null;
  }
}
