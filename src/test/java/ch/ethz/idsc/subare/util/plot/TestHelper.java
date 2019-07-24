// code by jph
package ch.ethz.idsc.subare.util.plot;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.jfree.chart.JFreeChart;

/* package */ enum TestHelper {
  ;
  public static void draw(JFreeChart jFreeChart) {
    BufferedImage bufferedImage = new BufferedImage(400, 200, BufferedImage.TYPE_INT_ARGB);
    jFreeChart.draw(bufferedImage.createGraphics(), new Rectangle2D.Double(0, 0, 400, 200));
  }
}
