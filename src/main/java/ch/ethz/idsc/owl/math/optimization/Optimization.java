// code by astoll
package ch.ethz.idsc.owl.math.optimization;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import ch.ethz.idsc.owl.math.order.OrderComparator;

/** Creates an optimization problem with a given cost functional vector
 * which map the feasible inputs onto an objective space,
 * where they can be evaluated according to a given OrderComparator.
 *
 * @param <T> type of inputs
 * @param <E> type of objectives */
public class Optimization<T, E> {
  // TODO ASTOLL inputs attribute of class or as parameter to function
  // TODO ASTOLL implement as interface
  Set<T> inputs;
  List<Function<T, E>> functionVector;
  OrderComparator<T> orderComparator;

  public Optimization(Set<T> inputs, List<Function<T, E>> functionVector, OrderComparator<T> orderComparator) {
    this.inputs = inputs;
    this.functionVector = functionVector;
    this.orderComparator = orderComparator;
  }

  /** Apply constraints to input set
   * @return List of feasible alternatives */
  public List<T> getFeasibleAlternatives() {
    return null;
  }

  /** Map the feasible inputs onto the objective space, e.g. f(X).
   * 
   * @return Image of feasible inputs with respect to cost functional vector */
  public Set<E> inputsInObjectiveSpace() {
    // return inputs.stream().map(functionVector).collect(Collectors.toSet());
    return null;
  }

  /** Gets the arguments, i.e the optimal solutions,
   * for the optimization problem <t>min f(X)</t>.
   * 
   * @return Set of optimal solutions of the OP */
  public Set<E> getOptimalSolutions() {
    return null;
  }

  /** Gets the arguments, i.e the optimal solutions,
   * for the optimization problem <t>min f(X)</t>.
   * 
   * @return Set of optimal values of the OP */
  public Set<E> getOptimalValues() {
    return null;
  }
}
