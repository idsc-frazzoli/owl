// code by jph
package ch.ethz.idsc.owl.data;

import java.util.Arrays;
import java.util.LinkedList;

import junit.framework.TestCase;

public class ListsTest extends TestCase {
  public void testSimple() {
    assertEquals(Lists.getLast(Arrays.asList(3, 2, 8)), (Integer) 8);
  }

  public void testFail() {
    try {
      Lists.getLast(new LinkedList<>());
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
