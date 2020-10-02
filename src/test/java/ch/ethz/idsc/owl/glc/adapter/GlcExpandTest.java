// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import ch.ethz.idsc.owl.math.AssertFail;
import junit.framework.TestCase;

public class GlcExpandTest extends TestCase {
  public void testFailNull() {
    AssertFail.of(() -> new GlcExpand(null));
  }
}
