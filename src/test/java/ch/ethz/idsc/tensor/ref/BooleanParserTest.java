// code by jph
package ch.ethz.idsc.tensor.ref;

import ch.ethz.idsc.owl.math.AssertFail;
import junit.framework.TestCase;

public class BooleanParserTest extends TestCase {
  public void testCase() {
    assertNull(BooleanParser.orNull("False"));
  }

  public void testBooleanToString() {
    assertEquals(Boolean.TRUE.toString(), "true");
    assertEquals(Boolean.FALSE.toString(), "false");
  }

  public void testNullFail() {
    AssertFail.of(() -> BooleanParser.orNull(null));
  }
}
