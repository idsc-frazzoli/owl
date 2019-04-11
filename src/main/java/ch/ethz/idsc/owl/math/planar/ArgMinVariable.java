// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Decrement;
import ch.ethz.idsc.tensor.sca.Increment;

public class ArgMinVariable implements Function<Tensor, Scalar> {
  private final TrajectoryEntryFinder entryFinder;
  private final Function<Tensor, Scalar> mapping;
  private final int maxLevel;
  // ---
  private final Comparator<Tensor> comparator;
  // ---
  private final Tensor[] pairs; // [{value, variable}, ...]

  /** @param entryFinder strategy
   * @param mapping cost function
   * @param maxLevel of search steps
   * @return ArgMinVariable */
  public static ArgMinVariable using(TrajectoryEntryFinder entryFinder, Function<Tensor, Scalar> mapping, int maxLevel) {
    return new ArgMinVariable(entryFinder, mapping, maxLevel);
  }

  // ---
  /** @param entryFinder strategy
   * @param mapping cost function
   * @param maxLevel of search steps */
  private ArgMinVariable(TrajectoryEntryFinder entryFinder, Function<Tensor, Scalar> mapping, int maxLevel) {
    this.entryFinder = entryFinder;
    this.mapping = mapping;
    this.maxLevel = maxLevel;
    Tensor placeholder = Tensors.of(RealScalar.of(Double.MAX_VALUE), entryFinder.uncorrectedInitialVar());
    pairs = new Tensor[] {placeholder, placeholder, placeholder};
    comparator = new Comparator<Tensor>() {
      @Override
      public int compare(Tensor t1, Tensor t2) {
        Scalar s1 = t1.Get(0);
        Scalar s2 = t2.Get(0);
        if (Scalars.lessThan(s1, s2))
          return -1;
        if (Scalars.lessThan(s2, s1))
          return 1;
        return 0;
      }
    };
  }

  @Override // from Function
  public Scalar apply(Tensor tensor) {
    entryFinder.initial(tensor).ifPresent(this::insert);
    Scalar initialVar = entryFinder.currentVar();
    if (tensor.length() < 2)
      return initialVar; // no bisection possible
    Function<Scalar, Optional<Tensor>> function = entryFinder.on(tensor);
    Tensor[] tmp = new Tensor[3];
    // search from initial upwards
    while (!Arrays.equals(pairs, tmp)) {
      tmp = pairs.clone();
      update(function, Increment.ONE.apply(entryFinder.currentVar()));
    }
    // search from initial downwards
    update(function, Decrement.ONE.apply(initialVar));
    while (!Arrays.equals(pairs, tmp)) {
      tmp = pairs.clone();
      update(function, Decrement.ONE.apply(entryFinder.currentVar()));
    }
    // bisect previously determined goal region
    return bisect(function, 0);
  }

  /** calculate and add pair {value, variable}
   * @param vector */
  private void insert(Tensor vector) {
    pairs[2] = Tensors.of(mapping.apply(vector), entryFinder.currentVar());
    Arrays.sort(pairs, comparator);
  }

  /** update pairs given variable
   * @param function pre-setup trajectory entry finder
   * @param var */
  private void update(Function<Scalar, Optional<Tensor>> function, Scalar var) {
    function.apply(var).ifPresent(this::insert);
  }

  /** @param function pre-setup trajectory entry finder
   * @param level current search depth
   * @return best variable */
  private Scalar bisect(Function<Scalar, Optional<Tensor>> function, int level) {
    Scalar var1 = pairs[0].Get(1);
    Scalar var2 = pairs[1].Get(1);
    if (var1.equals(var2) || level == maxLevel)
      return var1;
    update(function, Mean.of(Tensors.of(var1, var2)).Get());
    return bisect(function, level + 1);
  }
}
