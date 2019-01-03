// code by jph
package ch.ethz.idsc.owl.gui;

import java.awt.Color;

import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import junit.framework.TestCase;

public class ColorLookupTest extends TestCase {
  public void testSimple() {
    ColorDataIndexed colorDataIndexed = ColorLookup.hsluv_lightness(128, .5).deriveWithAlpha(76);
    assertEquals(colorDataIndexed.length(), 128);
    Color color0 = colorDataIndexed.getColor(0);
    assertEquals(color0.getRed(), 234);
    assertEquals(color0.getGreen(), 0);
    assertEquals(color0.getBlue(), 100);
    assertEquals(color0.getAlpha(), 76);
    Color color1 = colorDataIndexed.getColor(1);
    assertEquals(color1.getRed(), 235);
    assertEquals(color1.getGreen(), 0);
    assertEquals(color1.getBlue(), 89);
    assertEquals(color1.getAlpha(), 76);
  }
}
