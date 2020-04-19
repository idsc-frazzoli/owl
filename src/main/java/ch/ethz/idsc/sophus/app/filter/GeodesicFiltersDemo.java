// code by jph
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.GeodesicFilters;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.app.SmoothingKernel;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.lie.TensorProduct;

/* package */ class GeodesicFiltersDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DRAW = ColorDataLists._001.strict();
  private static final ColorDataIndexed COLOR_FILL = COLOR_DRAW.deriveWithAlpha(64);
  // ---
  protected final SpinnerLabel<SmoothingKernel> spinnerKernel = new SpinnerLabel<>();

  GeodesicFiltersDemo() {
    super(true, GeodesicDisplays.SE2C_SE2_R2);
    // ---
    timerFrame.jToolBar.addSeparator();
    {
      spinnerKernel.setList(Arrays.asList(SmoothingKernel.values()));
      spinnerKernel.setValue(SmoothingKernel.GAUSSIAN);
      spinnerKernel.addToComponentReduced(timerFrame.jToolBar, new Dimension(180, 28), "smoothing kernel");
    }
    setControlPointsSe2(TensorProduct.of(Range.of(0, 5), UnitVector.of(3, 0)).multiply(RealScalar.of(2)));
  }

  @Override // from RenderInterface
  public synchronized void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor control = getGeodesicControlPoints();
    if (control.length() % 2 == 1) {
      GeodesicDisplay geodesicDisplay = geodesicDisplay();
      SmoothingKernel smoothingKernel = spinnerKernel.getValue();
      for (GeodesicFilters geodesicFilters : GeodesicFilters.values()) {
        int ordinal = geodesicFilters.ordinal();
        Tensor mean = geodesicFilters.from(geodesicDisplay, smoothingKernel).apply(control);
        Color color = COLOR_DRAW.getColor(ordinal);
        PointsRender pointsRender = new PointsRender(COLOR_FILL.getColor(ordinal), color);
        pointsRender.show(geodesicDisplay::matrixLift, geodesicDisplay.shape(), Tensors.of(mean)).render(geometricLayer, graphics);
        graphics.setColor(color);
        graphics.drawString("" + geodesicFilters, 0, 32 + ordinal * 16);
      }
    }
  }

  public static void main(String[] args) {
    new GeodesicFiltersDemo().setVisible(1000, 600);
  }
}
