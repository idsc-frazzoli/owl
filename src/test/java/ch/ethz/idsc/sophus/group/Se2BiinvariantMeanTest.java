// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Se2BiinvariantMeanTest extends TestCase {
  public void testSimple() {
    Double root = Math.sqrt(2);
    Tensor p = Tensors.vector(-root / 2, root / 2, Math.PI / 4);
    Tensor q = Tensors.vector(0, root, 0);
    Tensor r = Tensors.vector(-root / 2, -root / 3, -Math.PI / 4);
    Tensor sequence = Tensors.of(p, q, r);
    Tensor weights = Tensors.vector(1, 1, 1).divide(RealScalar.of(3));
    // ---
    Double nom = Math.sqrt(2) - Math.PI / 4;
    Double denom = 1 + Math.PI / 4 * (Math.sqrt(2) / (2 - Math.sqrt(2)));
    Tensor expected = Tensors.vector(nom / denom, 0, 0);
    Tensor actual = Se2BiinvariantMean.INSTANCE.mean(sequence, weights);
    // Assert.assertEquals(expected, actual);
  }
}
