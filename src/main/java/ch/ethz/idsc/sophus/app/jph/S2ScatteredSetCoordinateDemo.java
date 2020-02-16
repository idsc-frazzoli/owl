// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.SnBarycentricCoordinates;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.img.ColorDataGradient;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class S2ScatteredSetCoordinateDemo extends ScatteredSetCoordinateDemo {
  private final JToggleButton jToggleLower = new JToggleButton("lower");
  private final JToggleButton jToggleAxes = new JToggleButton("axes");

  public S2ScatteredSetCoordinateDemo() {
    super(GeodesicDisplays.S2_ONLY, SnBarycentricCoordinates.values());
    {
      jToggleLower.setSelected(true);
      timerFrame.jToolBar.add(jToggleLower);
    }
    {
      jToggleAxes.setSelected(true);
      timerFrame.jToolBar.add(jToggleAxes);
    }
    setControlPointsSe2(Tensors.fromString("{{-5, 3, 0}, {3, 5, 0}, {-4, -3, 0}, {2, -3, 0}}"));
    setMidpointIndicated(false);
    timerFrame.configCoordinateOffset(500, 500);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    ColorDataGradient colorDataGradient = colorDataGradient();
    renderControlPoints(geometricLayer, graphics);
    graphics.setFont(ArrayPlotRender.FONT);
    graphics.setColor(Color.BLACK);
    graphics.drawString("" + spinnerBarycentric.getValue(), 0, 10 + 17);
    final Tensor controlPoints = getGeodesicControlPoints();
    S2GeodesicDisplay s2GeodesicDisplay = (S2GeodesicDisplay) geodesicDisplay();
    for (int index = 0; index < controlPoints.length(); ++index) {
      Tensor xyz = controlPoints.get(index);
      geometricLayer.pushMatrix(s2GeodesicDisplay.matrixLift(xyz));
      Point2D point2d = geometricLayer.toPoint2D(0, 0);
      graphics.drawString("  " + index, (int) point2d.getX(), (int) point2d.getY());
      geometricLayer.popMatrix();
    }
    // ---
    if (s2GeodesicDisplay.dimensions() < controlPoints.length()) {
      GraphicsUtil.setQualityHigh(graphics);
      Tensor sX = Subdivide.of(-1.0, +1.0, refinement());
      Tensor sY = Subdivide.of(-1.0, +1.0, refinement());
      int n = sX.length();
      boolean lower = jToggleLower.isSelected();
      BarycentricCoordinate barycentricCoordinate = barycentricCoordinate();
      Tensor wgs = Array.of(l -> DoubleScalar.INDETERMINATE, lower ? n * 2 : n, n, controlPoints.length());
      IntStream.range(0, n).parallel().forEach(c0 -> {
        Scalar x = sX.Get(c0);
        int c1 = 0;
        for (Tensor y : sY) {
          Optional<Tensor> optionalP = s2GeodesicDisplay.optionalZ(Tensors.of(x, y, RealScalar.ONE));
          if (optionalP.isPresent()) {
            Tensor point = optionalP.get();
            wgs.set(barycentricCoordinate.weights(controlPoints, point), n - c1 - 1, c0);
            if (lower) {
              point.set(Scalar::negate, 2);
              wgs.set(barycentricCoordinate.weights(controlPoints, point), n + n - c1 - 1, c0);
            }
          }
          ++c1;
        }
      });
      // ---
      if (jToggleHeatmap.isSelected()) { // render basis functions
        List<Integer> dims = Dimensions.of(wgs);
        Tensor _wgp = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
        new ArrayPlotRender(_wgp, colorDataGradient, 0, 32, 2).render(geometricLayer, graphics);
      }
    }
  }

  public static void main(String[] args) {
    new S2ScatteredSetCoordinateDemo().setVisible(1200, 900);
  }
}
