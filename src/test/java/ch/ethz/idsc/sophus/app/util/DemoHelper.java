// code by jph
package ch.ethz.idsc.sophus.app.util;

public enum DemoHelper {
  ;
  public static void brief(AbstractDemo abstractDemo) {
    abstractDemo.timerFrame.jFrame.setVisible(true);
    try {
      Thread.sleep(400);
    } catch (Exception exception) {
      throw new RuntimeException();
    }
    abstractDemo.timerFrame.jFrame.setVisible(false);
  }
}
