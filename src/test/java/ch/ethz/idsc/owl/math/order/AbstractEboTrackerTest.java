// code by jph
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class AbstractEboTrackerTest extends TestCase {
  public void testPermutations() {
    Tensor slackVector = Tensors.vector(1, 2, 0.5);
    Distribution distribution = UniformDistribution.of(0, 3);
    Tensor values = RandomVariate.of(distribution, 300, 3);
    final Set<Integer> minKeys1;
    {
      AbstractEboTracker<Integer> lexicographicSemiorderMinTracker = //
          (AbstractEboTracker<Integer>) SetEboTracker.<Integer>withSet(slackVector);
      for (int index = 0; index < values.length(); ++index)
        lexicographicSemiorderMinTracker.digest(index, values.get(index));
      minKeys1 = new HashSet<>(lexicographicSemiorderMinTracker.getMinKeys());
    }
    List<Integer> list = IntStream.range(0, values.length()).boxed().collect(Collectors.toList());
    for (int round = 0; round < 10; ++round) {
      Collections.shuffle(list);
      SetEboTracker<Integer> lsmtc = //
          (SetEboTracker<Integer>) SetEboTracker.<Integer>withSet(slackVector);
      for (int index = 0; index < values.length(); ++index) {
        int count = list.get(index);
        lsmtc.digest(count, values.get(count));
      }
      Collection<Integer> minKeys2 = lsmtc.getMinKeys();
      assertEquals(minKeys1, new HashSet<>(minKeys2));
    }
  }

  public void testEmptyPollFail() {
    Tensor slackVector = Tensors.vector(1, 2, 0.5);
    EboTracker<Integer> lexicographicSemiorderMinTracker = //
        SetEboTracker.<Integer>withSet(slackVector);
    AssertFail.of(() -> lexicographicSemiorderMinTracker.pollBestKey());
  }

  public void testMatrixSlackFail() {
    AssertFail.of(() -> SetEboTracker.withSet(HilbertMatrix.of(3)));
  }

  public void testScalarSlackFail() {
    AssertFail.of(() -> SetEboTracker.withSet(Pi.VALUE));
  }
}
