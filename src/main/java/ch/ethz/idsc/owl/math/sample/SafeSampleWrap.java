// code by gjoel
package ch.ethz.idsc.owl.math.sample;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.function.Predicate;

import ch.ethz.idsc.tensor.Tensor;

public class SafeSampleWrap {
  @SafeVarargs
  public static SafeSampleWrap with(Predicate<Tensor>... predicates) {
    return with(Arrays.asList(predicates));
  }

  public static SafeSampleWrap with(Collection<Predicate<Tensor>> predicates) {
    return new SafeSampleWrap(predicates);
  }

  // ---
  private final Collection<Predicate<Tensor>> predicates;

  private SafeSampleWrap(Collection<Predicate<Tensor>> predicates) {
    this.predicates = predicates;
  }

  public RandomSampleInterface apply(RandomSampleInterface randomSampleInterface) {
    return new RandomSampleInterface() {
      @Override
      public Tensor randomSample(Random random) {
        while (true) {
          Tensor sample = randomSampleInterface.randomSample(random);
          if (predicates.stream().anyMatch(predicate -> !predicate.test(sample)))
            continue;
          return sample;
        }
      }
    };
  }
}
