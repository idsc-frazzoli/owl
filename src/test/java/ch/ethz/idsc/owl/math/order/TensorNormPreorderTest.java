// code by jph
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class TensorNormPreorderTest extends TestCase {
  public void testSimple() {
    TensorNormPreorder tensorNormPreorder = new TensorNormPreorder(Norm.INFINITY);
    PreorderComparator<Tensor> preorderComparator = tensorNormPreorder.comparator();
    assertEquals(preorderComparator.compare(Tensors.vector(12, 3), Tensors.vector(3, 12)), PreorderComparison.INDIFFERENT);
    assertEquals(preorderComparator.compare(Tensors.vector(1, 3), Tensors.vector(3, 12)), PreorderComparison.LESS_EQUALS_ONLY);
  }
}
