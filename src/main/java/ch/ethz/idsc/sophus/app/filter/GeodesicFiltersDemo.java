// code by jph
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.PointsRender;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.opt.GeodesicFilters;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.ext.Integers;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.lie.TensorProduct;
import ch.ethz.idsc.tensor.sca.win.WindowFunctions;

/* package */ class GeodesicFiltersDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DRAW = ColorDataLists._001.strict();
  private static final ColorDataIndexed COLOR_FILL = COLOR_DRAW.deriveWithAlpha(64);
  // ---
  protected final SpinnerLabel<WindowFunctions> spinnerKernel = new SpinnerLabel<>();

  public GeodesicFiltersDemo() {
    super(true, GeodesicDisplays.SE2C_SE2_R2);
    // ---
    timerFrame.jToolBar.addSeparator();
    {
      spinnerKernel.setList(Arrays.asList(WindowFunctions.values()));
      spinnerKernel.setValue(WindowFunctions.GAUSSIAN);
      spinnerKernel.addToComponentReduced(timerFrame.jToolBar, new Dimension(180, 28), "smoothing kernel");
    }
    setControlPointsSe2(TensorProduct.of(Range.of(0, 5), UnitVector.of(3, 0)).multiply(RealScalar.of(2)));
  }

  @Override // from RenderInterface
  public synchronized void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor control = getGeodesicControlPoints();
    if (!Integers.isEven(control.length())) {
      ManifoldDisplay geodesicDisplay = manifoldDisplay();
      ScalarUnaryOperator smoothingKernel = spinnerKernel.getValue().get();
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
