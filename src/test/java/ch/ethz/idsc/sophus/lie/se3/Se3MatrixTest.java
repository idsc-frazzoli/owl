// code by jph
package ch.ethz.idsc.sophus.lie.se3;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.alg.Range;
import junit.framework.TestCase;

public class Se3MatrixTest extends TestCase {
  public void testRotation() {
    Tensor matrix = Partition.of(Range.of(0, 16), 4);
    Tensor rotate = Se3Matrix.rotation(matrix);
    assertEquals(rotate, Tensors.fromString("{{0, 1, 2}, {4, 5, 6}, {8, 9, 10}}"));
  }

  public void testTranslation() {
    Tensor matrix = Partition.of(Range.of(0, 16), 4);
    Tensor vector = Se3Matrix.translation(matrix);
    assertEquals(vector, Tensors.vector(3, 7, 11));
  }
}
