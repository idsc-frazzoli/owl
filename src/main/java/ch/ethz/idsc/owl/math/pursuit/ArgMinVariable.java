// code by gjoel
package ch.ethz.idsc.owl.math.pursuit;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.red.Mean;

public class ArgMinVariable implements TensorScalarFunction {
  /** @param trajectoryEntryFinder strategy
   * @param mapping cost function
   * @param maxLevel of search steps
   * @return ArgMinVariable */
  public static ArgMinVariable using(TrajectoryEntryFinder trajectoryEntryFinder, TensorScalarFunction mapping, int maxLevel) {
    return new ArgMinVariable(trajectoryEntryFinder, mapping, maxLevel);
  }

  /***************************************************/
  private final TrajectoryEntryFinder trajectoryEntryFinder;
  private final TensorScalarFunction mapping;
  private final int maxLevel;
  private final Tensor[] pairs; // [{value, variable}, ...]

  // ---
  /** @param trajectoryEntryFinder strategy
   * @param mapping cost function
   * @param maxLevel of search steps */
  private ArgMinVariable(TrajectoryEntryFinder trajectoryEntryFinder, TensorScalarFunction mapping, int maxLevel) {
    this.trajectoryEntryFinder = trajectoryEntryFinder;
    this.mapping = mapping;
    this.maxLevel = maxLevel;
    pairs = new Tensor[3];
  }

  @Override // from Function
  public Scalar apply(Tensor tensor) {
    trajectoryEntryFinder.sweep(tensor).forEach(this::insert);
    try {
      bisect(trajectoryEntryFinder.on(tensor), 0);
    } catch (NullPointerException e) {
      // ---
    }
    return pairs[0].Get(1);
  }

  /** calculate and add pair {value, variable}
   * @param trajectoryEntry */
  private void insert(TrajectoryEntry trajectoryEntry) {
    Optional<Tensor> optional = trajectoryEntry.point();
    if (optional.isPresent()) {
      Scalar cost = mapping.apply(optional.get());
      synchronized (pairs) {
        pairs[2] = Tensors.of(cost, trajectoryEntry.variable());
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
    // LONGTERM structure not optimal: isPresent, insert, isPresent
    if (trajectoryEntry.point().isPresent())
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
    update(function, (Scalar) Mean.of(Tensors.of(var1, var2)));
    return bisect(function, level + 1);
  }
}
