// code by jph
package ch.ethz.idsc.sophus.app.util;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;

public class DisplayHelper {
  private Rectangle screen = new Rectangle();

  public DisplayHelper() {
    GraphicsEnvironment myGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    for (GraphicsDevice myGraphicsDevice : myGraphicsEnvironment.getScreenDevices())
      for (GraphicsConfiguration myGraphicsConfiguration : myGraphicsDevice.getConfigurations())
        screen = screen.union(myGraphicsConfiguration.getBounds());
  }

  public Rectangle allVisible(int x, int y, int width, int height) {
    x = Math.max(0, Math.min(x, screen.width - width));
    y = Math.max(0, Math.min(y, screen.height - height));
    return new Rectangle(x, y, width, height);
  }

  public Rectangle allVisible(Rectangle myRectangle) {
    return allVisible(myRectangle.x, myRectangle.y, myRectangle.width, myRectangle.height);
  }

  public Rectangle getScreenRectangle() {
    return screen;
  }

  @Override
  public String toString() {
    return "Display point=(" + screen.x + ", " + screen.y + ") dimension=(" + screen.width + ", " + screen.height + ")";
  }

  public static Point getMouseLocation() {
    try {
      // can test with GraphicsEnvironment.isHeadless()
      return MouseInfo.getPointerInfo().getLocation();
    } catch (Exception myException) {
      myException.printStackTrace();
    }
    return new Point();
  }
}
