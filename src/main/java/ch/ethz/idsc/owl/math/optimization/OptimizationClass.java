// code by astoll
package ch.ethz.idsc.owl.math.optimization;

public interface OptimizationClass<T, E, R> {
  /** Apply constraints to input set
   * @return List of feasible alternatives */
  public Iterable<T> getFeasibleAlternatives();

  /** Map the feasible inputs onto the objective space, e.g. f(X).
   * 
   * @return Image of feasible inputs with respect to cost functional vector */
  public Iterable<E> inputsInObjectiveSpace();

  /** Gets the arguments, i.e the optimal solutions,
   * for the optimization problem <t>min f(X)</t>.
   * 
   * @return Set of optimal solutions of the OP */
  public Iterable<T> getOptimalSolutions();

  /** Gets the arguments, i.e the optimal solutions,
   * for the optimization problem <t>min f(X)</t>.
   * 
   * @return Set of optimal values of the OP */
  public Iterable<R> getOptimalValues();
}
