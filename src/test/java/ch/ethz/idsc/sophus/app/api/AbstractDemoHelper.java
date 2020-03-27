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
        abstractDemo.timerFrame.geometricComponent.getMouseSe2CState());
    BufferedImage bufferedImage = new BufferedImage(1280, 960, BufferedImage.TYPE_INT_ARGB);
    abstractDemo.render(geometricLayer, bufferedImage.createGraphics());
    boolean success = true;
    if (abstractDemo instanceof GeodesicDisplayDemo) {
      GeodesicDisplayDemo geodesicDisplayDemo = (GeodesicDisplayDemo) abstractDemo;
      for (GeodesicDisplay geodesicDisplay : geodesicDisplayDemo.getGeodesicDisplays())
        try {
          geodesicDisplayDemo.setGeodesicDisplay(geodesicDisplay);
          geodesicDisplayDemo.render(geometricLayer, bufferedImage.createGraphics());
        } catch (Exception exception) {
          System.err.println(geodesicDisplay);
          success = false;
        }
    }
    if (!success)
      throw new RuntimeException();
  }
}
