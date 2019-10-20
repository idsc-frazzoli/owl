// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;

public enum AbstractDemoHelper {
  ;
  /** off-screen test
   * 
   * @param abstractDemo */
  public static void offscreen(AbstractDemo abstractDemo) {
    GeometricLayer geometricLayer = new GeometricLayer( //
        abstractDemo.timerFrame.geometricComponent.getModel2Pixel(), //
        abstractDemo.timerFrame.geometricComponent.getMouseSe2State());
    BufferedImage bufferedImage = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
    if (abstractDemo instanceof GeodesicDisplayDemo) {
      GeodesicDisplayDemo geodesicDisplayDemo = (GeodesicDisplayDemo) abstractDemo;
      for (GeodesicDisplay geodesicDisplay : geodesicDisplayDemo.getGeodesicDisplays()) {
        geodesicDisplayDemo.setGeodesicDisplay(geodesicDisplay);
        abstractDemo.render(geometricLayer, bufferedImage.createGraphics());
      }
    }
  }

  public static void show(AbstractDemo abstractDemo) {
    abstractDemo.timerFrame.jFrame.setVisible(true);
    try {
      if (abstractDemo instanceof GeodesicDisplayDemo) {
        Thread.sleep(300);
        GeodesicDisplayDemo geodesicDisplayDemo = (GeodesicDisplayDemo) abstractDemo;
        for (GeodesicDisplay geodesicDisplay : geodesicDisplayDemo.getGeodesicDisplays()) {
          geodesicDisplayDemo.setGeodesicDisplay(geodesicDisplay);
          Thread.sleep(300);
        }
      } else
        Thread.sleep(400);
    } catch (Exception exception) {
      exception.printStackTrace();
      throw new RuntimeException();
    }
    abstractDemo.timerFrame.jFrame.setVisible(false);
  }
}
