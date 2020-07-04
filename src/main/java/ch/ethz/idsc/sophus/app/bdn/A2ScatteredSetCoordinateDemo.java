// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.img.ColorDataGradient;

/* package */ abstract class A2ScatteredSetCoordinateDemo extends ExportCoordinateDemo {
  private final JToggleButton jToggleAxes = new JToggleButton("axes");

  public A2ScatteredSetCoordinateDemo( //
      boolean addRemoveControlPoints, //
      List<GeodesicDisplay> list, //
      List<LogWeighting> array) {
    super(addRemoveControlPoints, list, array);
    {
      jToggleAxes.setSelected(true);
      timerFrame.jToolBar.add(jToggleAxes);
    }
  }

  @Override
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    ColorDataGradient colorDataGradient = colorDataGradient();
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    final Tensor sequence = getGeodesicControlPoints();
    LeversRender leversRender = LeversRender.of(geodesicDisplay(), operator(sequence), sequence, null, geometricLayer, graphics);
    leversRender.renderIndex();
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    // ---
    if (jToggleHeatmap.isSelected()) // render basis functions
      if (geodesicDisplay.dimensions() < sequence.length()) { // render basis functions
        Tensor origin = getGeodesicControlPoints();
        Tensor wgs = compute(operator(origin), refinement());
        List<Integer> dims = Dimensions.of(wgs);
        Tensor _wgp = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
        // RenderQuality.setQuality(graphics);
        ArrayPlotRender.rescale(_wgp, colorDataGradient, magnification()).render(geometricLayer, graphics);
      }
  }
}
