// code by jph
package ch.ethz.idsc.owl.math.sample;

import java.io.IOException;
import java.util.Arrays;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class BoxRandomSampleTest extends TestCase {
  public void testSimple3D() {
    Tensor offset = Tensors.vector(2, 2, 3);
    Tensor width = Tensors.vector(1, 1, 1);
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(offset.subtract(width), offset.add(width));
    Tensor samples = RandomSample.of(randomSampleInterface, 100);
    Scalars.compare(Norm._2.ofVector(Mean.of(samples).subtract(offset)), RealScalar.of(0.1));
    assertEquals(Dimensions.of(samples), Arrays.asList(100, 3));
  }

  public void testSingle() {
    Tensor offset = Tensors.vector(2, 2, 3);
    Tensor width = Tensors.vector(1, 1, 1);
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(offset.subtract(width), offset.add(width));
    Tensor rand = RandomSample.of(randomSampleInterface);
    assertEquals(Dimensions.of(rand), Arrays.asList(3));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(Tensors.vector(1, 2, 3), Tensors.vector(3, 4, 8));
    RandomSampleInterface copy = Serialization.copy(randomSampleInterface);
    Tensor tensor = RandomSample.of(copy);
    VectorQ.requireLength(tensor, 3);
  }

  public void testDimensionFail() {
    try {
      BoxRandomSample.of(Tensors.vector(1, 2), Tensors.vector(1, 2, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testSignFail() {
    try {
      BoxRandomSample.of(Tensors.vector(1, 2), Tensors.vector(2, 1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
