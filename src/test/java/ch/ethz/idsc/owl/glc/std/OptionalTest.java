// code by jph
package ch.ethz.idsc.owl.glc.std;

import java.util.Optional;

import junit.framework.TestCase;

public class OptionalTest extends TestCase {
  public void testSimple() {
    Optional<String> optional = Optional.ofNullable(null);
    assertFalse(optional.isPresent());
    assertEquals(optional.orElse(null), null);
  }

  public void testSimple2() {
    Optional<String> optional = Optional.ofNullable("asdfasdf");
    assertTrue(optional.isPresent());
  }
}
