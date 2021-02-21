// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Graphics2D;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.sophus.gds.GeodesicDisplayRender;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.hs.sn.SnManifold;
import ch.ethz.idsc.sophus.hs.sn.TSnProjection;
import ch.ethz.idsc.sophus.math.Exponential;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Flatten;

/* package */ class S2ExpDemo extends ControlPointsDemo {
  public S2ExpDemo() {
    super(true, GeodesicDisplays.S2_ONLY);
    // ---
    timerFrame.geometricComponent.addRenderInterfaceBackground(new GeodesicDisplayRender() {
      @Override
      public ManifoldDisplay getGeodesicDisplay() {
        return manifoldDisplay();
      }
    });
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    timerFrame.geometricComponent.setOffset(400, 400);
    // ---
    setControlPointsSe2(Tensors.fromString("{{-0.3, 0.0, 0}, {0.0, 0.5, 0.0}, {0.5, 0.5, 1}, {0.5, -0.4, 0}}"));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor points = getGeodesicControlPoints(0, 1);
    if (0 < points.length()) {
      Tensor origin = points.get(0);
      Exponential exponential = SnManifold.INSTANCE.exponential(origin);
      Tensor tSnProjection = TSnProjection.of(origin);
      Tensor m = Array.of(list -> exponential.exp(Tensors.vector(list).multiply(RealScalar.of(.25)).dot(tSnProjection)), 5, 5);
      Tensor sequence = Flatten.of(m, 1);
      LeversRender leversRender = //
          LeversRender.of(manifoldDisplay, sequence, origin, geometricLayer, graphics);
      leversRender.renderOrigin();
      leversRender.renderSequence();
    }
  }

  public static void main(String[] args) {
    new S2ExpDemo().setVisible(1000, 800);
  }
}
