// code by jph
package ch.ethz.idsc.owl.gui;

import java.awt.Color;

import ch.ethz.idsc.tensor.img.ColorDataGradients;
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

  public void testGrayIncr() {
    ColorDataIndexed colorDataIndexed = ColorLookup.increasing(3, ColorDataGradients.GRAYSCALE).deriveWithAlpha(76);
    assertEquals(colorDataIndexed.length(), 3);
    Color color0 = colorDataIndexed.getColor(0);
    assertEquals(color0.getRed(), 0);
    assertEquals(color0.getGreen(), 0);
    assertEquals(color0.getBlue(), 0);
    assertEquals(color0.getAlpha(), 76);
    Color color1 = colorDataIndexed.getColor(1);
    assertEquals(color1.getRed(), 128);
    assertEquals(color1.getGreen(), 128);
    assertEquals(color1.getBlue(), 128);
    assertEquals(color1.getAlpha(), 76);
    Color color2 = colorDataIndexed.getColor(2);
    assertEquals(color2.getRed(), 255);
    assertEquals(color2.getGreen(), 255);
    assertEquals(color2.getBlue(), 255);
    assertEquals(color2.getAlpha(), 76);
  }

  public void testGrayDecr() {
    ColorDataIndexed colorDataIndexed = ColorLookup.decreasing(3, ColorDataGradients.GRAYSCALE).deriveWithAlpha(76);
    assertEquals(colorDataIndexed.length(), 3);
    Color color2 = colorDataIndexed.getColor(2);
    assertEquals(color2.getRed(), 0);
    assertEquals(color2.getGreen(), 0);
    assertEquals(color2.getBlue(), 0);
    assertEquals(color2.getAlpha(), 76);
    Color color1 = colorDataIndexed.getColor(1);
    assertEquals(color1.getRed(), 128);
    assertEquals(color1.getGreen(), 128);
    assertEquals(color1.getBlue(), 128);
    assertEquals(color1.getAlpha(), 76);
    Color color0 = colorDataIndexed.getColor(0);
    assertEquals(color0.getRed(), 255);
    assertEquals(color0.getGreen(), 255);
    assertEquals(color0.getBlue(), 255);
    assertEquals(color0.getAlpha(), 76);
  }
}
