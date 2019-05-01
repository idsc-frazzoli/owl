// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Arrays;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Decrement;
import ch.ethz.idsc.tensor.sca.Increment;

public class ArgMinVariable implements TensorScalarFunction {
  private final TrajectoryEntryFinder entryFinder;
  private final TensorScalarFunction mapping;
  private final int maxLevel;
  // ---
  private final Tensor[] pairs; // [{value, variable}, ...]

  /** @param entryFinder strategy
   * @param mapping cost function
   * @param maxLevel of search steps
   * @return ArgMinVariable */
  public static ArgMinVariable using(TrajectoryEntryFinder entryFinder, TensorScalarFunction mapping, int maxLevel) {
    return new ArgMinVariable(entryFinder, mapping, maxLevel);
  }

  // ---
  /** @param entryFinder strategy
   * @param mapping cost function
   * @param maxLevel of search steps */
  private ArgMinVariable(TrajectoryEntryFinder entryFinder, TensorScalarFunction mapping, int maxLevel) {
    this.entryFinder = entryFinder;
    this.mapping = mapping;
    this.maxLevel = maxLevel;
    pairs = new Tensor[3];
  }

  @Override // from Function
  public Scalar apply(Tensor tensor) {
    TrajectoryEntry initial = entryFinder.initial(tensor);
    insert(initial);
    if (tensor.length() < 2)
      return initial.variable; // no bisection possible
    Function<Scalar, TrajectoryEntry> function = entryFinder.on(tensor);
    Tensor[] tmp = new Tensor[3];
    TrajectoryEntry entry = initial;
    // search from initial upwards
    while (!Arrays.equals(pairs, tmp)) {
      tmp = pairs.clone();
      entry = update(function, Increment.ONE.apply(entry.variable));
    }
    // search from initial downwards
    entry = update(function, Decrement.ONE.apply(initial.variable));
    while (!Arrays.equals(pairs, tmp)) {
      tmp = pairs.clone();
      entry = update(function, Decrement.ONE.apply(entry.variable));
    }
    // bisect previously determined goal region
    return bisect(function, 0);
  }

  /** calculate and add pair {value, variable}
   * @param entry */
  private void insert(TrajectoryEntry entry) {
    entry.point.ifPresent(point -> {
      pairs[2] = Tensors.of(mapping.apply(point), entry.variable);
      Arrays.sort(pairs, ArgMinComparator.INSTANCE);
    });
  }

  /** update pairs given variable
   * @param function pre-setup trajectory entry finder
   * @param var
   * @return TrajectoryEntry */
  private TrajectoryEntry update(Function<Scalar, TrajectoryEntry> function, Scalar var) {
    TrajectoryEntry entry = function.apply(var);
    entry.point.ifPresent(p -> insert(entry));
    return entry;
  }

  /** @param function pre-setup trajectory entry finder
   * @param level current search depth
   * @return best variable */
  private Scalar bisect(Function<Scalar, TrajectoryEntry> function, int level) {
    Scalar var1 = pairs[0].Get(1);
    Scalar var2 = pairs[1].Get(1);
    if (var1.equals(var2) || level == maxLevel)
      return var1;
    update(function, Mean.of(Tensors.of(var1, var2)).Get());
    return bisect(function, level + 1);
  }
}
