// code by jph
package ch.ethz.idsc.tensor.acm;

import ch.ethz.idsc.owl.math.AssertFail;
import junit.framework.TestCase;

public class TensorsExtTest extends TestCase {
  public void testSimple() {
    AssertFail.of(() -> TensorsExt.of("abc"));
  }
}
