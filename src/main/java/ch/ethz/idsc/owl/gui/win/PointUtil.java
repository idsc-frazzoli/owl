// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.Point;

enum PointUtil {
  ;
  static int inftyNorm(Point p1, Point p2) {
    return Math.max(Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y));
  }
}
