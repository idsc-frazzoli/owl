// code by jph
package ch.ethz.idsc.sophus.app.clothoid;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.Se2ClothoidDisplay;
import ch.ethz.idsc.sophus.crv.clothoid.EulerSpiral;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;

/* package */ class EulerSpiralDemo extends ControlPointsDemo {
  private static final PointsRender POINTS_RENDER_P = new PointsRender(new Color(128, 128, 128, 64), new Color(128, 128, 128, 128));
  private final RenderInterface renderInterface;

  public EulerSpiralDemo() {
    super(false, GeodesicDisplays.R2_ONLY);
    Tensor points = Subdivide.of(-10., 10., 10000).map(EulerSpiral.FUNCTION);
    renderInterface = new PathRender(Color.BLUE, 1f) //
        .setCurve(points, false);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    renderInterface.render(geometricLayer, graphics);
    {
      Tensor points = Subdivide.of(-3., 3., 50).map(EulerSpiral.FUNCTION);
      POINTS_RENDER_P.show(Se2ClothoidDisplay.INSTANCE::matrixLift, Arrowhead.of(0.03), points) //
          .render(geometricLayer, graphics);
    }
  }

  public static void main(String[] args) {
    new EulerSpiralDemo().setVisible(1000, 600);
  }
}
