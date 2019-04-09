// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Increment;

public class ArgMinVariable implements Function<Tensor, Scalar> {
  private final TrajectoryEntryFinder entryFinder;
  private final Function<Tensor, Scalar> mapping;
  private final int maxLevel;
  // ---
  private final Comparator<Tensor> comparator;
  // ---
  private Tensor pairs = Tensors.empty(); // {{value, variable}, ...}

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
    comparator = new Comparator<Tensor>() {
      @Override
      public int compare(Tensor t1, Tensor t2) {
        Scalar s1 = t1.Get(0);
        Scalar s2 = t2.Get(0);
        if (Scalars.lessThan(s1, s2))
          return -1;
        if (Scalars.lessThan(s2, s1))
          return 1;
        GlobalAssert.that(s1.equals(s2));
        return 0;
      }
    };
  }

  @Override // from Function
  public Scalar apply(Tensor tensor) {
    insertIfPresent(entryFinder.initial(tensor));
    // fill in initial pairs
    Function<Scalar, Optional<Tensor>> function = entryFinder.on(tensor);
    int count = 0;
    while (pairs.length() < 3) {
      GlobalAssert.that(count < tensor.length());
      Scalar next = Increment.ONE.apply(entryFinder.currentVar());
      insertIfPresent(function.apply(next));
      count++;
    }
    // bisection
    pairs = Sort.of(pairs, comparator);
    return bisect(function, 0);
  }

  /** @param tensor to be processed and appended */
  private void insertIfPresent(Optional<Tensor> tensor) {
    tensor.map(mapping).ifPresent(s -> pairs.append(Tensors.of(s, entryFinder.currentVar())));
  }

  /** @param function pre-setup trajectory entry finder
   * @param level current search depth
   * @return best variable */
  private Scalar bisect(Function<Scalar, Optional<Tensor>> function, int level) {
    Scalar var1 = pairs.Get(0, 1);
    Scalar var2 = pairs.Get(1, 1);
    if (var1.equals(var2) || level == maxLevel)
      return var1;
    Scalar step = Abs.of(var1.subtract(var2)).multiply(RealScalar.of(0.5));
    pairs.append(Tensors.of(function.apply(var1.subtract(step)).map(mapping).get(), entryFinder.currentVar()));
    pairs.append(Tensors.of(function.apply(var1.add(step)).map(mapping).get(), entryFinder.currentVar()));
    pairs = Sort.of(pairs, comparator).extract(0, 3);
    return bisect(function, level + 1);
  }
}
