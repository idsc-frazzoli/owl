// code by jph
package ch.ethz.idsc.sophus.gui.win;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplayDemo;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;

public enum AbstractDemoHelper {
  ;
  /** off-screen test
   * 
   * @param abstractDemo
   * @throws IllegalAccessException
   * @throws IllegalArgumentException */
  public static void offscreen(AbstractDemo abstractDemo) {
    // try {
    // Class<? extends AbstractDemo> class2 = abstractDemo.getClass();
    // System.out.println(class2);
    // for (Field field : class2.getFields()) {
    // FieldTest annotation = field.getAnnotation(FieldTest.class);
    // if (Objects.nonNull(annotation)) {
    // System.out.println(field.getName());
    // System.out.println(annotation);
    // Class<?> class1 = field.getType();
    // System.out.println(class1);
    // System.out.println(class1.equals(SpinnerLabel.class));
    // try {
    // Object sl = field.get(abstractDemo);
    // System.out.println(sl.getClass());
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // }
    // } catch (Exception exception) {
    // throw new RuntimeException(exception);
    // }
    GeometricLayer geometricLayer = new GeometricLayer( //
        abstractDemo.timerFrame.geometricComponent.getModel2Pixel(), //
        abstractDemo.timerFrame.geometricComponent.getMouseSe2CState());
    BufferedImage bufferedImage = new BufferedImage(1280, 960, BufferedImage.TYPE_INT_ARGB);
    abstractDemo.render(geometricLayer, bufferedImage.createGraphics());
    boolean success = true;
    if (abstractDemo instanceof GeodesicDisplayDemo) {
      GeodesicDisplayDemo geodesicDisplayDemo = (GeodesicDisplayDemo) abstractDemo;
      for (ManifoldDisplay geodesicDisplay : geodesicDisplayDemo.getGeodesicDisplays())
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
