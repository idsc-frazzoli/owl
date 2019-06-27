// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;

import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Tensor;

abstract class GokartDemo extends Se2Demo {
  static final Tensor ARROWHEAD = Arrowhead.of(0.6);
  static final Color COLOR_WAYPOINT = new Color(64, 192, 64, 64);
}
