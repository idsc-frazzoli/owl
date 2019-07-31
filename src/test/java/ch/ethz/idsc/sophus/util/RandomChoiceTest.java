// code by jph
package ch.ethz.idsc.sophus.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RandomChoiceTest extends TestCase {
  public void testSimple() {
    Set<Integer> set = new HashSet<>();
    for (int index = 0; index < 100; ++index) {
      int value = RandomChoice.of(Arrays.asList(1, 2, 3, 4));
      set.add(value);
    }
    assertEquals(set.size(), 4);
  }

  public void testTensor() {
    Scalar scalar = RandomChoice.of(Tensors.vector(2, 5));
    assertTrue(scalar.equals(RealScalar.of(2)) || scalar.equals(RealScalar.of(5)));
  }

  public void testTensorEmptyFail() {
    try {
      RandomChoice.of(Tensors.vector());
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      RandomChoice.of(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
