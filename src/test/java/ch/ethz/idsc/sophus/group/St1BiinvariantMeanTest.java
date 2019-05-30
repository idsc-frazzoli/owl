// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.Assert;
import junit.framework.TestCase;

public class St1BiinvariantMeanTest extends TestCase {
  public void testTrivial() {
    Tensor sequence = Tensors.of(Tensors.vector(2, 2));
    Tensor weights = Tensors.vector(1);
    Tensor actual = St1BiinvariantMean.INSTANCE.mean(sequence, weights);
    Assert.assertEquals(Tensors.vector(2, 2), actual);
  }

  public void testSimple() {
    Tensor p = Tensors.vector(1, 2);
    Tensor q = Tensors.vector(2, 3);
    Tensor sequence = Tensors.of(p, q);
    Tensor weights = Tensors.vector(0.5, 0.5);
    Tensor actual = St1BiinvariantMean.INSTANCE.mean(sequence, weights);
    Tensor expected = Tensors.fromString("{1.414213562373095, 2.414213562373095}");
    Assert.assertEquals(expected, actual);
  }
}
