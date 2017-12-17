// code by jph
package ch.ethz.idsc.owl.gui;

import junit.framework.TestCase;

public class ColorLookupTest extends TestCase {
  public void testSimple() {
    ColorLookup colorLookup = ColorLookup.hsluv_lightness(.5, .3);
    colorLookup.get(0.0);
    colorLookup.get(0.5);
    colorLookup.get(1.0);
    assertTrue(colorLookup.get(0.0).equals(colorLookup.get(1.0)));
    assertFalse(colorLookup.get(0.0).equals(colorLookup.get(0.8)));
    assertTrue(colorLookup.get(0.2).equals(colorLookup.get(0.2)));
  }
}
