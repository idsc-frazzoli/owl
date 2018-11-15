// code by jph
package ch.ethz.idsc.owl.gui;

import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import junit.framework.TestCase;

public class ColorLookupTest extends TestCase {
  public void testSimple() {
    ColorDataIndexed colorDataIndexed = ColorLookup.hsluv_lightness(.5).deriveWithAlpha(76);
    // TODO test
  }
}
