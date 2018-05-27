// code by jph
package ch.ethz.idsc.owl.gui;

import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import junit.framework.TestCase;

public class ColorLookupTest extends TestCase {
  public void testSimple() {
    ColorDataIndexed colorDataIndexed = ColorLookup.hsluv_lightness(.5).deriveWithAlpha(76);
    colorDataIndexed.rescaled(0.0);
    colorDataIndexed.rescaled(0.5);
    colorDataIndexed.rescaled(1.0);
    assertTrue(colorDataIndexed.rescaled(0.0).equals(colorDataIndexed.rescaled(1.0)));
    assertFalse(colorDataIndexed.rescaled(0.0).equals(colorDataIndexed.rescaled(0.8)));
    assertTrue(colorDataIndexed.rescaled(0.2).equals(colorDataIndexed.rescaled(0.2)));
  }
}
