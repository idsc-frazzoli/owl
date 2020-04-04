// code by jph
package ch.ethz.idsc.sophus.app.clothoid;

import java.awt.Graphics2D;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.ply.Spearhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

/* package */ class SpearheadDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(128);

  public SpearheadDemo() {
    super(false, GeodesicDisplays.SE2_ONLY);
    // ---
    timerFrame.geometricComponent.addRenderInterface(AxesRender.INSTANCE);
    // ---
    setControlPointsSe2(Tensors.fromString("{{-0.5, -0.5, 0.3}}"));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor control = getGeodesicControlPoints();
    renderControlPoints(geometricLayer, graphics);
    Tensor curve = Spearhead.of(control.get(0), RealScalar.of(geometricLayer.pixel2modelWidth(10)));
    graphics.setColor(COLOR_DATA_INDEXED.getColor(1));
    graphics.fill(geometricLayer.toPath2D(curve));
    new PathRender(COLOR_DATA_INDEXED.getColor(0), 1.5f) //
        .setCurve(curve, false) //
        .render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new SpearheadDemo().setVisible(1000, 800);
  }
}
