// code by ob, jph
package ch.ethz.idsc.sophus.lie.st;

import ch.ethz.idsc.sophus.lie.BiinvariantMeanTestHelper;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.lie.Permutations;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class StBiinvariantMeanTest extends TestCase {
  public void testTrivial() {
    Tensor sequence = Tensors.of(Tensors.vector(2, 2));
    Tensor weights = Tensors.vector(1);
    Tensor actual = StBiinvariantMean.INSTANCE.mean(sequence, weights);
    assertEquals(Tensors.vector(2, 2), actual);
  }

  public void testSimple() {
    Tensor p = Tensors.vector(1, 2);
    Tensor q = Tensors.vector(2, 3);
    Tensor sequence = Tensors.of(p, q);
    Tensor weights = Tensors.vector(0.5, 0.5);
    Tensor actual = StBiinvariantMean.INSTANCE.mean(sequence, weights);
    Tensor expected = Tensors.fromString("{1.414213562373095, 2.414213562373095}");
    Chop._12.requireClose(expected, actual);
  }

  public void testReorder() {
    Tensor p = Tensors.vector(1, 2);
    Tensor q = Tensors.vector(2, 3);
    Tensor r = Tensors.vector(3, 1);
    Tensor sequence = Tensors.of(p, q, r);
    Tensor mask = Tensors.vector(1, 2, 3);
    Tensor weights = mask.divide(Total.ofVector(mask));
    Tensor actual = StBiinvariantMean.INSTANCE.mean(sequence, weights);
    Tensor expected = Tensors.vector(2.1822472719434427, 1.9243978173573888);
    Chop._12.requireClose(expected, actual);
    for (Tensor perm : Permutations.of(Range.of(0, weights.length()))) {
      int[] index = Primitives.toIntArray(perm);
      Tensor result = StBiinvariantMean.INSTANCE.mean(BiinvariantMeanTestHelper.order(sequence, index), BiinvariantMeanTestHelper.order(weights, index));
      Chop._12.requireClose(result, actual);
    }
  }

  public void testReorderNegative() {
    Tensor p = Tensors.vector(1, 2);
    Tensor q = Tensors.vector(2, 3);
    Tensor r = Tensors.vector(3, 1);
    Tensor s = Tensors.vector(0.5, -1);
    Tensor sequence = Tensors.of(p, q, r, s);
    Tensor mask = Tensors.vector(1, 2, 3, -1);
    Tensor weights = mask.divide(Total.ofVector(mask));
    Tensor actual = StBiinvariantMean.INSTANCE.mean(sequence, weights);
    Tensor expected = Tensors.vector(2.9301560515835217, 3.1983964535982485);
    Chop._12.requireClose(expected, actual);
    for (Tensor perm : Permutations.of(Range.of(0, weights.length()))) {
      int[] index = Primitives.toIntArray(perm);
      Tensor result = StBiinvariantMean.INSTANCE.mean(BiinvariantMeanTestHelper.order(sequence, index), BiinvariantMeanTestHelper.order(weights, index));
      Chop._12.requireClose(result, actual);
    }
  }

  public void testReorderNegativeVector() {
    Tensor p = Tensors.fromString("{1, {2, 3}}");
    Tensor q = Tensors.fromString("{2, {3, 1}}");
    Tensor r = Tensors.fromString("{3, {1, -3}}");
    Tensor s = Tensors.fromString("{0.5, {4, 5}}");
    Tensor sequence = Tensors.of(p, q, r, s);
    Tensor mask = Tensors.vector(1, 2, 3, -1);
    Tensor weights = mask.divide(Total.ofVector(mask));
    Tensor actual = StBiinvariantMean.INSTANCE.mean(sequence, weights);
    Tensor expected = Tensors.fromString("{2.9301560515835217, {1.0099219737525793, -2.5153382244082483}}");
    Chop._12.requireClose(expected, actual);
    for (Tensor perm : Permutations.of(Range.of(0, weights.length()))) {
      int[] index = Primitives.toIntArray(perm);
      Tensor result = StBiinvariantMean.INSTANCE.mean(BiinvariantMeanTestHelper.order(sequence, index), BiinvariantMeanTestHelper.order(weights, index));
      Chop._12.requireClose(result, actual);
    }
  }
}
