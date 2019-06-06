// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.lie.Permutations;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class So2BiinvariantMeanTest extends TestCase {
  public void testPermutations() {
    for (int length = 1; length < 6; ++length) {
      Distribution distribution = UniformDistribution.of(Clips.absolute(Math.PI));
      // here, we hope that no antipodal points are generated
      Tensor sequence = RandomVariate.of(distribution, length);
      Tensor weights = RandomVariate.of(UniformDistribution.unit(), length);
      weights = weights.divide(Total.ofVector(weights));
      Tensor solution = So2BiinvariantMean.INSTANCE.mean(sequence, weights);
      for (Tensor perm : Permutations.of(Range.of(0, weights.length()))) {
        int[] index = Primitives.toIntArray(perm);
        Tensor result = So2BiinvariantMean.INSTANCE.mean(TestHelper.order(sequence, index), TestHelper.order(weights, index));
        Chop._12.requireClose(result, solution);
      }
    }
  }

  public void testNonAffineFail() {
    try {
      So2BiinvariantMean.INSTANCE.mean(Tensors.vector(1, 1, 1), Tensors.vector(1, 1, 1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testEmptyFail() {
    try {
      So2BiinvariantMean.INSTANCE.mean(Tensors.empty(), Tensors.empty());
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testMatrixFail() {
    try {
      So2BiinvariantMean.INSTANCE.mean(HilbertMatrix.of(3), Tensors.vector(1, 1, 1).divide(RealScalar.of(3)));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
