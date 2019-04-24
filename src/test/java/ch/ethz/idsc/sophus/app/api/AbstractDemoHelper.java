// code by jph
package ch.ethz.idsc.sophus.app.api;

public enum AbstractDemoHelper {
  ;
  public static void brief(AbstractDemo abstractDemo) {
    abstractDemo.timerFrame.jFrame.setVisible(true);
    try {
      if (abstractDemo instanceof ControlPointsDemo) {
        Thread.sleep(200);
        ControlPointsDemo controlPointsDemo = (ControlPointsDemo) abstractDemo;
        for (GeodesicDisplay geodesicDisplay : controlPointsDemo.getGeodesicDisplays()) {
          controlPointsDemo.setGeodesicDisplay(geodesicDisplay);
          Thread.sleep(200);
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
