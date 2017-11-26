// code by jph
package ch.ethz.idsc.owl.gui;

import java.util.ArrayDeque;
import java.util.Deque;

import junit.framework.TestCase;

public class GeometricLayerTest extends TestCase {
  public void testSimple() {
    Deque<Integer> ad = new ArrayDeque<>();
    ad.push(2);
    ad.push(4);
    ad.push(9);
    assertEquals((int) ad.peek(), 9);
    ad.pop();
    assertEquals((int) ad.peek(), 4);
    ad.pop();
    assertEquals((int) ad.peek(), 2);
    ad.pop();
    assertEquals(ad.peek(), null);
  }
}
