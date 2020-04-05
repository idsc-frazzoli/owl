// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Graphics2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class BarycentricLeversDemo extends ControlPointsDemo {
  private final JToggleButton jToggleMean = new JToggleButton("mean");
  private final JToggleButton jToggleLevers = new JToggleButton("levers");

  public BarycentricLeversDemo() {
    super(true, GeodesicDisplays.ALL);
    setMidpointIndicated(false);
    {
      timerFrame.jToolBar.add(jToggleMean);
      timerFrame.jToolBar.add(jToggleLevers);
    }
    setControlPointsSe2(Tensors.fromString("{{-1, -2, 0}, {3, -2, -1}, {4, 2, 1}, {-1, 3, 2}, {-2, -3, -2}}"));
    // setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1, 0, 0}, {2, 0, 0}, {3, 0, 0}}"));
    setGeodesicDisplay(R2GeodesicDisplay.INSTANCE);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor controlPointsAll = getGeodesicControlPoints();
    if (0 < controlPointsAll.length()) {
      LeverRender leverRender = LeverRender.of( //
          geodesicDisplay, controlPointsAll.extract(1, controlPointsAll.length()), //
          controlPointsAll.get(0), geometricLayer, graphics);
      leverRender.renderLevers();
      leverRender.renderWeights();
      leverRender.renderSequence();
      leverRender.renderOrigin();
    }
  }

  public static void main(String[] args) {
    new BarycentricLeversDemo().setVisible(1200, 600);
  }
}