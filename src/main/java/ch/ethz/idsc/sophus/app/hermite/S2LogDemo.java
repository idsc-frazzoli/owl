// code by jph
package ch.ethz.idsc.sophus.app.hermite;

import java.awt.Graphics2D;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplayRender;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Drop;

/* package */ class S2LogDemo extends ControlPointsDemo {
  public S2LogDemo() {
    super(true, GeodesicDisplays.S2_ONLY);
    // ---
    timerFrame.geometricComponent.addRenderInterfaceBackground(new GeodesicDisplayRender() {
      @Override
      public GeodesicDisplay getGeodesicDisplay() {
        return geodesicDisplay();
      }
    });
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    timerFrame.configCoordinateOffset(400, 400);
    // ---
    setControlPointsSe2(Tensors.fromString("{{-0.3, 0.0, 0}, {0.0, 0.5, 0.0}, {0.5, 0.5, 1}, {0.5, -0.4, 0}}"));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    S2GeodesicDisplay geodesicDisplay = (S2GeodesicDisplay) geodesicDisplay();
    Tensor points = getGeodesicControlPoints();
    if (0 < points.length()) {
      Tensor x = points.get(0);
      Tensor sequence = Drop.head(points, 1);
      LeversRender leversRender = LeversRender.of(geodesicDisplay, sequence, x, geometricLayer, graphics);
      leversRender.renderLevers();
      leversRender.renderOrigin();
      leversRender.renderSequence();
      leversRender.renderTangentsPtoX(true);
      leversRender.renderTangentsXtoP(true);
    }
  }

  public static void main(String[] args) {
    new S2LogDemo().setVisible(1000, 800);
  }
}
