// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.math.WeightingInterface;
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
    renderControlPoints(geometricLayer, graphics);
    graphics.setFont(ArrayPlotRender.FONT);
    graphics.setColor(Color.BLACK);
    graphics.drawString("" + spinnerWeighting.getValue(), 0, 10 + 17);
    final Tensor controlPoints = getGeodesicControlPoints();
    int index = 0;
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor shape = geodesicDisplay.shape();
    graphics.setFont(ArrayPlotRender.FONT);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    graphics.setColor(Color.BLACK);
    for (Tensor q : controlPoints) {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(q));
      Rectangle rectangle = geometricLayer.toPath2D(shape, true).getBounds();
      graphics.drawString(" " + (index + 1), //
          rectangle.x + rectangle.width, //
          rectangle.y + rectangle.height + (-rectangle.height + fheight) / 2);
      geometricLayer.popMatrix();
      ++index;
    }
    // ---
    if (geodesicDisplay.dimensions() < controlPoints.length()) { // render basis functions
      VectorLogManifold flattenLogManifold = geodesicDisplay.flattenLogManifold();
      WeightingInterface weightingInterface = weightingInterface(flattenLogManifold);
      Tensor wgs = compute(weightingInterface, refinement());
      List<Integer> dims = Dimensions.of(wgs);
      Tensor _wgp = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
      RenderQuality.setQuality(graphics);
      new ArrayPlotRender(_wgp, colorDataGradient, 0, 32, magnification()).render(geometricLayer, graphics);
    }
  }
}
