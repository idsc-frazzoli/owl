// code by ynager
package ch.ethz.idsc.owl.mapping;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.util.function.BiConsumer;

/* package */ enum StaticHelper {
  ;
  static void makeStroke(Area area, float radius, BiConsumer<Area, Area> function) {
    Stroke stroke = new BasicStroke(radius * 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
    Shape strokeShape = stroke.createStrokedShape(area);
    Area strokeArea = new Area(strokeShape);
    function.accept(area, strokeArea);
  }

  static byte[] toAGRB(Color color) {
    return new byte[] { //
        (byte) color.getAlpha(), //
        (byte) color.getGreen(), //
        (byte) color.getRed(), //
        (byte) color.getBlue() };
  }
}
