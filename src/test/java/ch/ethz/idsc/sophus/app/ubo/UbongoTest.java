// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import junit.framework.TestCase;

public class UbongoTest extends TestCase {
  public void testSimple() {
    assertEquals(Ubongo.values().length, 12);
    assertEquals(Ubongo.C2.count(), 5);
  }

  public void testStamps() {
    for (Ubongo ubongo : Ubongo.values()) {
      // System.out.println();
      System.out.println(ubongo + " " + ubongo.stamps().size());
    }
  }
}
