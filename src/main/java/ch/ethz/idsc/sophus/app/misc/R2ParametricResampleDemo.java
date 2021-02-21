// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gui.ren.PointsRender;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.ply.d2.ParametricResample;
import ch.ethz.idsc.sophus.ply.d2.ResampleResult;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.ref.gui.ConfigPanel;

public class R2ParametricResampleDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.strict().deriveWithAlpha(128);
  private static final PointsRender POINTS_RENDER = new PointsRender(new Color(0, 128, 128, 64), new Color(0, 128, 128, 255));
  // ---
  public Scalar threshold = RealScalar.of(3);
  public Scalar ds = RealScalar.of(0.3);

  public R2ParametricResampleDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    // ---
    Container container = timerFrame.jFrame.getContentPane();
    ConfigPanel configPanel = ConfigPanel.of(this);
    container.add("West", configPanel.getFields());
    // ---
    int n = 20;
    setControlPointsSe2(PadRight.zeros(n, 3).apply(CirclePoints.of(n).multiply(RealScalar.of(3))));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    Tensor control = getGeodesicControlPoints();
    graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
    graphics.setStroke(new BasicStroke(2f));
    graphics.draw(geometricLayer.toPath2D(control));
    renderControlPoints(geometricLayer, graphics);
    // ---
    ParametricResample parametricResample = new ParametricResample(threshold, ds);
    ResampleResult resampleResult = parametricResample.apply(control);
    for (Tensor points : resampleResult.getPoints())
      POINTS_RENDER.show(manifoldDisplay()::matrixLift, manifoldDisplay().shape(), points) //
          .render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new R2ParametricResampleDemo().setVisible(1000, 600);
  }
}
