// code by astoll
package ch.ethz.idsc.owl.math.optimization;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.math.order.MinTracker;
import ch.ethz.idsc.owl.math.order.OrderComparator;
import ch.ethz.idsc.owl.math.order.TransitiveMinTracker;
import ch.ethz.idsc.tensor.Tensor;

/** Creates an optimization problem with a given cost functional vector
 * which map the feasible inputs onto an objective space,
 * where they can be evaluated according to a given OrderComparator. */
public final class TransitiveLexicographicOptimization implements OptimizationClass<Tensor, Tensor, Tensor> {
  private final List<Tensor> inputs;
  private final List<Function<Tensor, Tensor>> featureFunctionVector;
  private final List<Function<Tensor, Tensor>> scoringFunctionVector;
  private final MinTracker<Iterable<? extends Object>> minTracker;

  /** TODO ASTOLL document
   * 
   * @param inputs
   * @param featureFunctionVector
   * @param scoringFunctionVector
   * @param orderComparator */
  public TransitiveLexicographicOptimization( //
      List<Tensor> inputs, //
      List<Function<Tensor, Tensor>> featureFunctionVector, //
      List<Function<Tensor, Tensor>> scoringFunctionVector, //
      OrderComparator<Iterable<? extends Object>> orderComparator) {
    this.inputs = inputs;
    this.featureFunctionVector = featureFunctionVector;
    this.scoringFunctionVector = scoringFunctionVector;
    this.minTracker = TransitiveMinTracker.withSet(orderComparator);
  }

  /** Apply constraints to input set
   * 
   * @return List of feasible alternatives */
  @Override
  public Iterable<Tensor> getFeasibleAlternatives() {
    return inputs;
  }

  /** Maps the input element x into the objective space
   * 
   * @param x
   * @return Tensor x in Objective space */
  /* package */ Tensor getElementInObjectiveSpace(Tensor x) {
    return Tensor.of(featureFunctionVector.stream().map(function -> function.apply(x)));
  }

  /** Map the feasible inputs onto the objective space, e.g. f(X).
   * 
   * @return Image of feasible inputs with respect to cost functional vector */
  @Override
  public Set<Tensor> inputsInObjectiveSpace() {
    // return inputs.stream().map(functionVector).collect(Collectors.toSet());
    return inputs.stream() //
        .map(this::getElementInObjectiveSpace) //
        .collect(Collectors.toSet());
  }

  /* package */ Tensor getScoreOfObjectives(Tensor x) {
    return Tensor.of(scoringFunctionVector.stream().map(index -> index.apply(x)));
  }

  /** Map the feasible inputs onto the objective space, e.g. f(X).
   * 
   * @return Image of feasible inputs with respect to cost functional vector */
  private Stream<Tensor> scoresOfElements() {
    // return inputs.stream().map(functionVector).collect(Collectors.toSet());
    return inputsInObjectiveSpace().stream() //
        .map(this::getScoreOfObjectives);
  }

  /** Gets the arguments, i.e the optimal solutions,
   * for the optimization problem <t>min f(X)</t>.
   * 
   * @return Set of optimal solutions of the OP */
  @Override
  public List<Tensor> getOptimalSolutions() {
    scoresOfElements().forEach(minTracker::digest);
    return inputs.stream() //
        .filter(x -> getOptimalValues().contains(getElementInObjectiveSpace(x))) //
        .collect(Collectors.toList());
  }

  /** Gets the arguments, i.e the optimal solutions,
   * for the optimization problem <t>min f(X)</t>.
   * 
   * @return Set of optimal values of the OP */
  @Override
  public List<Tensor> getOptimalValues() {
    scoresOfElements().forEach(minTracker::digest);
    return inputsInObjectiveSpace().stream() //
        .filter(x -> minTracker.getMinElements().contains(getScoreOfObjectives(x))) //
        .collect(Collectors.toList());
  }
}
