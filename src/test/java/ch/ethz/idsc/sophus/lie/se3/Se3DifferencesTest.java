// code by jph
package ch.ethz.idsc.sophus.lie.se3;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.lie.Rodrigues;
import junit.framework.TestCase;

public class Se3DifferencesTest extends TestCase {
  public void testSimple() {
    Tensor m1 = Se3Matrix.of(Rodrigues.exp(Tensors.vector(0.2, -0.3, 0.4)), Tensors.vector(10, 20, 30));
    Tensor m2 = Se3Matrix.of(Rodrigues.exp(Tensors.vector(-0.2, 0.3, 0.4)), Tensors.vector(11, 21, 31));
    Tensor tensor = Se3Differences.INSTANCE.apply(Tensors.of(m1, m2));
    assertEquals(Dimensions.of(tensor), Arrays.asList(1, 2, 3));
  }
}
