// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.alg.Range;
import junit.framework.TestCase;

public class Se3UtilsTest extends TestCase {
  public void testRotation() {
    Tensor matrix = Partition.of(Range.of(0, 16), 4);
    Tensor rotate = Se3Utils.rotation(matrix);
    assertEquals(rotate, Tensors.fromString("{{0, 1, 2}, {4, 5, 6}, {8, 9, 10}}"));
  }

  public void testTranslation() {
    Tensor matrix = Partition.of(Range.of(0, 16), 4);
    Tensor vector = Se3Utils.translation(matrix);
    assertEquals(vector, Tensors.vector(3, 7, 11));
  }
}
