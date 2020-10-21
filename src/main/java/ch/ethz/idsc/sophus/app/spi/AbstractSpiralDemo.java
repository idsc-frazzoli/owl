// code by jph
package ch.ethz.idsc.sophus.app.spi;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.Se2ClothoidDisplay;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;

/* package */ abstract class AbstractSpiralDemo extends ControlPointsDemo {
  private static final PointsRender POINTS_RENDER = //
      new PointsRender(new Color(128, 128, 128, 64), new Color(128, 128, 128, 128));
  private static final Tensor SEPARATORS = Subdivide.of(-3.0, 3.0, 50);
  // ---
  private final ScalarTensorFunction scalarTensorFunction;
  private final RenderInterface renderInterface;

  public AbstractSpiralDemo(ScalarTensorFunction scalarTensorFunction) {
    super(false, GeodesicDisplays.R2_ONLY);
    this.scalarTensorFunction = scalarTensorFunction;
    Tensor points = Subdivide.of(-10.0, 10.0, 10000).map(scalarTensorFunction);
    renderInterface = new PathRender(Color.BLUE, 1f).setCurve(points, false);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    renderInterface.render(geometricLayer, graphics);
    Tensor points = SEPARATORS.map(scalarTensorFunction);
    POINTS_RENDER.show(Se2ClothoidDisplay.ANALYTIC::matrixLift, Arrowhead.of(0.03), points) //
        .render(geometricLayer, graphics);
  }
}
