// code by jph
package ch.ethz.idsc.owl.demo.noise;

import java.awt.Graphics2D;
import java.util.Arrays;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplayDemo;
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;

/* package */ class ColoredNoiseDemo extends GeodesicDisplayDemo {
  public ColoredNoiseDemo() {
    super(Arrays.asList(R2GeodesicDisplay.INSTANCE));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // TODO Auto-generated method stub
  }
}
