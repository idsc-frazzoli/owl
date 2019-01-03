// code by jph
package ch.ethz.idsc.sophus.app.api;

public enum DemoHelper {
  ;
  public static void brief(AbstractDemo abstractDemo) {
    abstractDemo.timerFrame.jFrame.setVisible(true);
    try {
      if (abstractDemo instanceof ControlPointsDemo) {
        Thread.sleep(200);
        ControlPointsDemo controlPointsDemo = (ControlPointsDemo) abstractDemo;
        for (int index = 0; index < controlPointsDemo.geodesicDisplaySpinner.numel(); ++index) {
          controlPointsDemo.geodesicDisplaySpinner.setIndex(index);
          Thread.sleep(200);
        }
      } else
        Thread.sleep(400);
    } catch (Exception exception) {
      throw new RuntimeException();
    }
    abstractDemo.timerFrame.jFrame.setVisible(false);
  }
}
