// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.stream.Collectors;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.lie.r2.ParametricResample;
import ch.ethz.idsc.sophus.lie.r2.ResampleResult;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;

/* package */ class R2ParametricResampleDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.strict().deriveWithAlpha(128);
  // ---
  private static final Tensor BETAS = Tensors.fromString("{1, 2, 5, 10,33, 100}");
  private final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();
  // private final PathRender pathRenderHull = new PathRender(COLOR_DATA_INDEXED.getColor(1), 1.5f);

  public R2ParametricResampleDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    // ---
    {
      spinnerBeta.setList(BETAS.stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setValue(RealScalar.of(1));
      spinnerBeta.addToComponentReduced(timerFrame.jToolBar, new Dimension(70, 28), "beta");
      // spinnerBeta.addSpinnerListener(v -> recompute());
    }
    // timerFrame.geometricComponent.addRenderInterface(pathRenderHull);
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
    {
      ParametricResample parametricResample = new ParametricResample(spinnerBeta.getValue(), RealScalar.of(0.3));
      ResampleResult resampleResult = parametricResample.apply(control);
      PointsRender pointsRender = new PointsRender(new Color(0, 128, 128, 64), new Color(0, 128, 128, 255));
      System.out.println(resampleResult.getPoints().size());
      for (Tensor points : resampleResult.getPoints()) {
        System.out.println(Dimensions.of(points));
        pointsRender.show(geodesicDisplay()::matrixLift, geodesicDisplay().shape(), points) //
            .render(geometricLayer, graphics);
      }
    }
  }

  public static void main(String[] args) {
    new R2ParametricResampleDemo().setVisible(1000, 600);
  }
}
