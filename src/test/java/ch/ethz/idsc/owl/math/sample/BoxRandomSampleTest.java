// code by jph
package ch.ethz.idsc.owl.math.sample;

import java.util.Arrays;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class BoxRandomSampleTest extends TestCase {
  public void testSimple3D() {
    Tensor offset = Tensors.vector(2, 2, 3);
    Tensor width = Tensors.vector(1, 1, 1);
    RandomSampleInterface randomSampleInterface = new BoxRandomSample(offset.subtract(width), offset.add(width));
    Tensor samples = RandomSample.of(randomSampleInterface, 100);
    Scalars.compare(Norm._2.ofVector(Mean.of(samples).subtract(offset)), RealScalar.of(.1));
    assertEquals(Dimensions.of(samples), Arrays.asList(100, 3));
  }

  public void testSingle() {
    Tensor offset = Tensors.vector(2, 2, 3);
    Tensor width = Tensors.vector(1, 1, 1);
    RandomSampleInterface rsi = new BoxRandomSample(offset.subtract(width), offset.add(width));
    Tensor rand = RandomSample.of(rsi);
    assertEquals(Dimensions.of(rand), Arrays.asList(3));
  }

  public void testFail() {
    try {
      new BoxRandomSample(Tensors.vector(1, 2), Tensors.vector(1, 2, 3));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
