// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.lie.Permutations;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class Se2CoveringBiinvariantMeanTest extends TestCase {
  public void testPermutations() {
    for (int length = 1; length < 6; ++length) {
      Distribution distribution = UniformDistribution.of(Clips.absolute(10));
      Tensor sequence = RandomVariate.of(distribution, length, 3);
      Tensor weights = RandomVariate.of(distribution, length);
      weights = weights.divide(Total.ofVector(weights));
      Tensor solution = Se2CoveringBiinvariantMean.INSTANCE.mean(sequence, weights);
      for (Tensor perm : Permutations.of(Range.of(0, weights.length()))) {
        int[] index = Primitives.toIntArray(perm);
        Tensor result = Se2CoveringBiinvariantMean.INSTANCE.mean(TestHelper.order(sequence, index), TestHelper.order(weights, index));
        Chop._12.requireClose(result, solution);
      }
    }
  }
}
