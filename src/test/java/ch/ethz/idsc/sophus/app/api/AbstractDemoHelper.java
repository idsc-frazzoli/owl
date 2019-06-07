// code by jph
package ch.ethz.idsc.sophus.app.api;

public enum AbstractDemoHelper {
  ;
  public static void brief(AbstractDemo abstractDemo) {
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
