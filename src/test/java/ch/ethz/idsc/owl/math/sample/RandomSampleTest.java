// code by jph
package ch.ethz.idsc.owl.math.sample;

import java.util.Arrays;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class RandomSampleTest extends TestCase {
  public void testSimple() {
    Tensor tensor = RandomSample.of(SphereRandomSample.of(Tensors.vector(1, 2, 3), RealScalar.ONE), 6);
    assertEquals(Dimensions.of(tensor), Arrays.asList(6, 3));
  }
}
