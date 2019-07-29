// code by jph
package ch.ethz.idsc.owl.math.sample;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

  public void testPermutations() {
    Tensor vector = Tensors.vector(1, 2, 3).unmodifiable();
    Set<Tensor> set = new HashSet<>();
    for (int index = 0; index < 100; ++index)
      set.add(RandomSample.of(vector));
    assertEquals(set.size(), 6);
  }
}
