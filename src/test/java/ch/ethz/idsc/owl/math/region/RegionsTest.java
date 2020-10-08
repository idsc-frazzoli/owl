// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.IOException;

import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class RegionsTest extends TestCase {
  public void testSimple() {
    assertTrue(Regions.completeRegion().isMember(null));
    assertFalse(Regions.emptyRegion().isMember(null));
  }

  public void testSerialization() throws ClassNotFoundException, IOException {
    assertTrue(Serialization.copy(Regions.completeRegion()).isMember(null));
    assertFalse(Serialization.copy(Regions.emptyRegion()).isMember(null));
  }
}
