// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Drop;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.opt.Pi;

/* package */ class Se2ScatteredSetCoordinateDemo extends ExportCoordinateDemo {
  private final JToggleButton jToggleAxes = new JToggleButton("axes");

  public Se2ScatteredSetCoordinateDemo() {
    super(true, GeodesicDisplays.SE2C_SE2, LogWeightings.list());
    spinnerRefine.setValueSafe(15);
    {
      jToggleAxes.setSelected(true);
      timerFrame.jToolBar.add(jToggleAxes);
    }
    Tensor se2 = Tensors.fromString("{{-1.5, 1.3, -2.3}, {+1.5, +1.3, 2.3}, {0.3, 1.5, 1.2}, {0.0, 0.5, -0.5}, {-1.4, -1.3, 0.1}, {1.2, -1.3, -1.2}}");
    // Tensor del = RandomVariate.of(UniformDistribution.of(0.00, 0.1),Dimensions.of(se2));
    setControlPointsSe2(se2);
    setMidpointIndicated(false);
    // Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    // timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    timerFrame.configCoordinateOffset(500, 500);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // if (jToggleAxes.isSelected())
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
      FlattenLogManifold flattenLogManifold = geodesicDisplay().flattenLogManifold();
      WeightingInterface weightingInterface = weightingInterface(flattenLogManifold);
      Tensor wgs = compute(weightingInterface, refinement());
      List<Integer> dims = Dimensions.of(wgs);
      Tensor _wgp = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
      RenderQuality.setQuality(graphics);
      new ArrayPlotRender(_wgp, colorDataGradient, 0, 32, magnification()).render(geometricLayer, graphics);
    }
  }

  @Override
  public Tensor compute(WeightingInterface weightingInterface, int refinement) {
    Tensor sX = Subdivide.of(-3.0, +3.0, refinement);
    Tensor sY = Subdivide.of(+3.0, -3.0, refinement);
    Tensor sA = Drop.tail(Subdivide.of(Pi.VALUE.negate(), Pi.VALUE, 6), 1);
    int n = sX.length();
    final Tensor origin = getGeodesicControlPoints();
    Tensor wgs = Array.of(l -> DoubleScalar.INDETERMINATE, n * sA.length(), n, origin.length());
    IntStream.range(0, n).parallel().forEach(c0 -> {
      Scalar x = sX.Get(c0);
      int ofs = 0;
      for (Tensor a : sA) {
        int c1 = 0;
        for (Tensor y : sY) {
          Tensor point = Tensors.of(x, y, a);
          wgs.set(weightingInterface.weights(origin, point), ofs + c1, c0);
          ++c1;
        }
        ofs += n;
      }
    });
    return wgs;
  }

  public static void main(String[] args) {
    new Se2ScatteredSetCoordinateDemo().setVisible(1200, 900);
  }
}