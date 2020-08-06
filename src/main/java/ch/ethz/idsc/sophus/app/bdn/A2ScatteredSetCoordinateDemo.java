// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Graphics2D;
import java.util.List;
import java.util.Objects;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.ArrayPlotRender;
import ch.ethz.idsc.sophus.app.api.GeodesicArrayPlot;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Transpose;

/* package */ abstract class A2ScatteredSetCoordinateDemo extends ExportCoordinateDemo {
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  // ---
  private RenderInterface renderInterface;

  public A2ScatteredSetCoordinateDemo(List<LogWeighting> array) {
    super(true, GeodesicDisplays.R2_H2_S2, array);
    // ---
    timerFrame.jToolBar.add(jToggleAxes);
    jToggleHeatmap.setVisible(false);
    jToggleArrows.setVisible(false);
  }

  @Override
  protected final void recompute() {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor sequence = getGeodesicControlPoints();
    if (geodesicDisplay.dimensions() < sequence.length()) { // render basis functions
      GeodesicArrayPlot geodesicArrayPlot = geodesicDisplay.geodesicArrayPlot();
      Tensor fallback = ConstantArray.of(DoubleScalar.INDETERMINATE, sequence.length());
      Tensor wgs = geodesicArrayPlot.raster(refinement(), operator(sequence), fallback);
      List<Integer> dims = Dimensions.of(wgs);
      Tensor wgp = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
      renderInterface = ArrayPlotRender.rescale(wgp, colorDataGradient(), magnification());
    } else
      renderInterface = null;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    final Tensor sequence = getGeodesicControlPoints();
    LeversRender leversRender = //
        LeversRender.of(geodesicDisplay(), sequence, null, geometricLayer, graphics);
    leversRender.renderIndexX();
    leversRender.renderIndexP();
    // ---
    if (Objects.isNull(renderInterface))
      recompute();
    if (Objects.nonNull(renderInterface))
      renderInterface.render(geometricLayer, graphics);
  }
}
