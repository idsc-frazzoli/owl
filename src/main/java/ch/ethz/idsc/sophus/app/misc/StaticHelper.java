// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Color;

import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

enum StaticHelper {
  ;
  static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  static final Color TICKS_COLOR = new Color(0, 0, 0, 128);
}
