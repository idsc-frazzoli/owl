// code by jph
package ch.ethz.idsc.owl.bot.util;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class LexicographicTest extends TestCase {
  public void testSimple1() {
    int c1 = Lexicographic.COMPARATOR.compare(Tensors.vector(0, 1, 2, 3), Tensors.vector(3, 0, 1, 2));
    int c2 = Integer.compare(0, 3);
    assertEquals(c1, -1);
    assertEquals(c1, c2);
  }

  public void testSimple2() {
    int c1 = Lexicographic.COMPARATOR.compare(Tensors.vector(0, 1, 2, 3), Tensors.vector(0, 1, 2, 2));
    int c2 = Integer.compare(3, 2);
    assertEquals(c1, c2);
  }

  public void testSimple3() {
    int c1 = Lexicographic.COMPARATOR.compare(Tensors.vector(0, 1, 2, 3), Tensors.vector(0, 1, 2, 4));
    // System.out.println(c1);
    int c2 = Integer.compare(3, 4);
    assertEquals(c1, c2);
  }

  public void testSimple4() {
    int c1 = Lexicographic.COMPARATOR.compare(Tensors.vector(0, 1, 2, 3), Tensors.vector(0, 1, 2, 3));
    int c2 = Integer.compare(3, 3);
    assertEquals(c1, c2);
    assertEquals(c1, 0);
  }

  public void testFail() {
    try {
      Lexicographic.COMPARATOR.compare(Tensors.vector(0, 1, 2, 3), Tensors.vector(0, 1, 2, 4, 2));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
