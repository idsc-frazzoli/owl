// code by jph
package ch.ethz.idsc.owl.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class RandomElementsTest extends TestCase {
  public void testSimple() {
    Random random = new Random();
    List<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    for (int d = 1; d <= 3; ++d) {
      Set<Scalar> set = new HashSet<>();
      int pow = (int) Math.round(Math.pow(10, d));
      for (int count = 0; count < pow; ++count) {
        List<Integer> elements = RandomElements.of(list, d, random);
        ScalarUnaryOperator scalarUnaryOperator = Series.of(Tensors.vector(elements));
        Scalar scalar = scalarUnaryOperator.apply(RealScalar.of(10));
        set.add(scalar);
      }
      assertTrue(pow / 3 <= set.size());
    }
  }

  public void testEmpty() {
    List<Integer> elements = RandomElements.of(Arrays.asList(), 3);
    assertEquals(elements, Arrays.asList());
    try {
      elements.add(2);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testSize1() {
    List<Integer> elements = RandomElements.of(Arrays.asList(2), 3);
    assertEquals(elements, Arrays.asList(2));
    try {
      elements.add(2);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testSize2() {
    List<Integer> elements = RandomElements.of(Arrays.asList(2, 3), 3);
    assertEquals(elements, Arrays.asList(2, 3));
    try {
      elements.add(2);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testSize3() {
    List<Integer> elements = RandomElements.of(Arrays.asList(2, 3, 8), 3);
    assertEquals(elements, Arrays.asList(2, 3, 8));
    try {
      elements.add(2);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testSize4() {
    List<Integer> elements = RandomElements.of(Arrays.asList(2, 3, 8, 2), 3);
    assertEquals(elements.size(), 3);
    try {
      elements.add(2);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testSize5() {
    RandomElements.of(Arrays.asList(2, 3, 8, 2), 0);
    try {
      RandomElements.of(Arrays.asList(2, 3, 8, 2), -1);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      RandomElements.of(Arrays.asList(), -1);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
