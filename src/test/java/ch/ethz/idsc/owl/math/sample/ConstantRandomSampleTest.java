// code by jph
package ch.ethz.idsc.owl.math.sample;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ConstantRandomSampleTest extends TestCase {
  public void testSimple() {
    Tensor v = Tensors.vector(2, 3);
    RandomSampleInterface rsi = new ConstantRandomSample(v);
    assertEquals(RandomSample.of(rsi), v);
  }
}
