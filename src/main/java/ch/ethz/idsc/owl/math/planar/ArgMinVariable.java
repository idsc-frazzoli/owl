// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Arrays;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.red.Mean;

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
    entryFinder.sweep(tensor).parallel().forEach(this::insert);
    try {
      bisect(entryFinder.on(tensor), 0);
    } catch (NullPointerException e) {
      // ---
    }
    return pairs[0].Get(1);
  }

  /** calculate and add pair {value, variable}
   * @param trajectoryEntry */
  private void insert(TrajectoryEntry trajectoryEntry) {
    if (trajectoryEntry.point.isPresent()) {
      Scalar cost = mapping.apply(trajectoryEntry.point.get());
      synchronized (pairs) {
        pairs[2] = Tensors.of(cost, trajectoryEntry.variable);
        Arrays.sort(pairs, ArgMinComparator.INSTANCE);
      }
    }
  }

  /** update pairs given variable
   * 
   * @param function pre-setup trajectory entry finder
   * @param var
   * @return TrajectoryEntry */
  private TrajectoryEntry update(Function<Scalar, TrajectoryEntry> function, Scalar var) {
    TrajectoryEntry trajectoryEntry = function.apply(var);
    if (trajectoryEntry.point.isPresent())
      insert(trajectoryEntry);
    return trajectoryEntry;
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
