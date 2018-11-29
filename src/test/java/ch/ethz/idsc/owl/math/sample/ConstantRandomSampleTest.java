// code by jph
package ch.ethz.idsc.owl.math.sample;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class ConstantRandomSampleTest extends TestCase {
  public void testSimple() {
    Tensor tensor = HilbertMatrix.of(2, 3);
    RandomSampleInterface randomSampleInterface = new ConstantRandomSample(tensor);
    assertEquals(RandomSample.of(randomSampleInterface), tensor);
  }
}
