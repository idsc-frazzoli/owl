// code by astoll
package ch.ethz.idsc.owl.math.optimization;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.math.order.GenericLexicographicComparator;
import ch.ethz.idsc.owl.math.order.MinTracker;
import ch.ethz.idsc.owl.math.order.TransitiveMinTracker;
import ch.ethz.idsc.tensor.Tensor;

/** Creates an optimization problem with a given cost functional vector
 * which map the feasible inputs onto an objective space,
 * where they can be evaluated according to a given OrderComparator.
 *
 * @param <T> type of inputs
 * @param <E> type of objectives */
public class TransitiveLexicographicOptimization implements OptimizationClass<Tensor ,Tensor, Tensor> {
  List<Tensor> inputs;
  List<Function<Tensor, Tensor>> featureFunctionVector;
  List<Function<Tensor, Tensor>> scoringFunctionVector;
  GenericLexicographicComparator orderComparator;
  MinTracker<Iterable<? extends Object>> minTracker;

  public TransitiveLexicographicOptimization(List<Tensor> inputs, List<Function<Tensor, Tensor>> featureFunctionVector, List<Function<Tensor, Tensor>> scoringFunctionVector,
      GenericLexicographicComparator orderComparator) {
    this.inputs = inputs;
    this.featureFunctionVector = featureFunctionVector;
    this.scoringFunctionVector = scoringFunctionVector;
    this.orderComparator = orderComparator;
    this.minTracker = TransitiveMinTracker.withList(orderComparator);
  }

  /** Apply constraints to input set
   * @return List of feasible alternatives */
  public Iterable<Tensor> getFeasibleAlternatives() {
    return inputs;
  }

  /**
   * Maps the input element x into the objective space
   * 
   * @param x
   * @return Tensor x in Objectve space
   */
  public Tensor getElementInObjectiveSpace(Tensor x){
    Stream<? extends Tensor> stream = featureFunctionVector.stream().map(index-> index.apply(x));
    return Tensor.of(stream);
  }

  /** Map the feasible inputs onto the objective space, e.g. f(X).
   * 
   * @return Image of feasible inputs with respect to cost functional vector */
  public Set<Tensor> inputsInObjectiveSpace() {
    //return inputs.stream().map(functionVector).collect(Collectors.toSet());
    return inputs.stream().map(x -> getElementInObjectiveSpace(x)).collect(Collectors.toSet());
  }
  
  public Tensor getScoreOfObjectives(Tensor o){
    Stream<? extends Tensor> stream = scoringFunctionVector.stream().map(index-> index.apply(o));
    return Tensor.of(stream);
  }
  
  /** Map the feasible inputs onto the objective space, e.g. f(X).
   * 
   * @return Image of feasible inputs with respect to cost functional vector */
  public Set<Tensor> ScoresOfElements() {
    //return inputs.stream().map(functionVector).collect(Collectors.toSet());
    return inputsInObjectiveSpace().stream().map(x -> getScoreOfObjectives(x)).collect(Collectors.toSet());
  }

  /** Gets the arguments, i.e the optimal solutions,
   * for the optimization problem <t>min f(X)</t>.
   * 
   * @return Set of optimal solutions of the OP */
  public List<Tensor> getOptimalSolutions() {
    Iterator<Tensor> iterator = ScoresOfElements().iterator();
    while(iterator.hasNext()) {
      minTracker.digest(iterator.next());
    }
    return inputs.stream().filter(x-> getOptimalValues().contains(getElementInObjectiveSpace(x))).collect(Collectors.toList());
  }

  /** Gets the arguments, i.e the optimal solutions,
   * for the optimization problem <t>min f(X)</t>.
   * 
   * @return Set of optimal values of the OP */
  public List<Tensor> getOptimalValues() {
    Iterator<Tensor> iterator = ScoresOfElements().iterator();
    while(iterator.hasNext()) {
      minTracker.digest(iterator.next());
    }
    return inputsInObjectiveSpace().stream().filter(x-> minTracker.getMinElements().contains(getScoreOfObjectives(x))).collect(Collectors.toList());
  }
}
