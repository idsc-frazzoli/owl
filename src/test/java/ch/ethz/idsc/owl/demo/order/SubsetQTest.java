// code by jph
package ch.ethz.idsc.owl.demo.order;

import java.util.Arrays;

import junit.framework.TestCase;

public class SubsetQTest extends TestCase {
  public void testSimple() {
    assertTrue(SubsetQ.of(Arrays.asList(1, 2, 3), Arrays.asList(3, 1)));
  }
}
