// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.noise.SimplexContinuousNoise;
import ch.ethz.idsc.sophus.app.api.ArrayRender;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.RnBarycentricCoordinates;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.red.Entrywise;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class R2ScatteredSetCoordinateDemo extends ScatteredSetCoordinateDemo {
  private final JToggleButton jToggleButtonAxes = new JToggleButton("axes");
  private final JToggleButton jToggleButtonArrows = new JToggleButton("arrows");
  private final JToggleButton jToggleAnimate = new JToggleButton("animate");
  private final Timing timing = Timing.started();
  // ---
  private Tensor snapshot;

  public R2ScatteredSetCoordinateDemo() {
    super(GeodesicDisplays.SE2C_SPD2_S2_R2, RnBarycentricCoordinates.SCATTERED);
    {
      jToggleButtonAxes.setSelected(true);
      timerFrame.jToolBar.add(jToggleButtonAxes);
      jToggleButtonArrows.setSelected(true);
      timerFrame.jToolBar.add(jToggleButtonArrows);
    }
    {
      jToggleAnimate.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (jToggleAnimate.isSelected())
            snapshot = getControlPointsSe2();
          else
            setControlPointsSe2(snapshot);
        }
      });
      timerFrame.jToolBar.add(jToggleAnimate);
    }
    setControlPointsSe2(Tensors.fromString("{{2, -3, 1.5}, {3, 5, 1}, {-4, -3, 1}, {-5, 3, 2}}"));
    timerFrame.configCoordinateOffset(500, 500);
  }

  private static Tensor random(double toc, int index) {
    return Tensors.vector( //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 0), //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 1), //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 2) * 2);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ColorDataGradient colorDataGradient = colorDataGradient();
    if (jToggleButtonAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    setMidpointIndicated(!jToggleAnimate.isSelected());
    if (jToggleAnimate.isSelected()) {
      double toc = timing.seconds() * 0.3;
      int n = snapshot.length();
      Tensor control = Tensors.reserve(n);
      for (int index = 0; index < n; ++index) { //
        control.append(snapshot.get(index).add(random(toc, index)));
      }
      setControlPointsSe2(control);
    }
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor controlPoints = getGeodesicControlPoints();
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    if (2 < controlPoints.length()) {
      Tensor domain = Tensor.of(controlPoints.stream().map(geodesicDisplay::toPoint));
      GraphicsUtil.setQualityHigh(graphics);
      BarycentricCoordinate barycentricCoordinate = barycentricCoordinate();
      Tensor min = Entrywise.min().of(domain).map(RealScalar.of(0.01)::add);
      Tensor max = Entrywise.max().of(domain).map(RealScalar.of(0.01)::subtract).negate();
      min = Tensors.vector(-5, -5);
      max = Tensors.vector(+5, +5);
      Tensor sX = Subdivide.of(min.Get(0), max.Get(0), refinement());
      Tensor sY = Subdivide.of(min.Get(1), max.Get(1), refinement());
      int n = sX.length();
      Tensor[][] array = new Tensor[n][n];
      Tensor[][] point = new Tensor[n][n];
      Tensor wgs = Array.of(l -> DoubleScalar.INDETERMINATE, n, n, domain.length());
      IntStream.range(0, sX.length()).parallel().forEach(c0 -> {
        Tensor x = sX.get(c0);
        int c1 = 0;
        for (Tensor y : sY) {
          Tensor px = Tensors.of(x, y);
          Tensor weights = barycentricCoordinate.weights(domain, px);
          wgs.set(weights, n - c1 - 1, c0);
          Tensor mean = biinvariantMean.mean(controlPoints, weights);
          array[c0][c1] = mean;
          point[c0][c1] = geodesicDisplay.toPoint(mean);
          ++c1;
        }
        ++c0;
      });
      new ArrayRender(point, colorDataGradient.deriveWithOpacity(RationalScalar.HALF)).render(geometricLayer, graphics);
      // ---
      if (jToggleHeatmap.isSelected()) { // render basis functions
        List<Integer> dims = Dimensions.of(wgs);
        Tensor _wgs = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
        BufferedImage bufferedImage = ImageFormat.of(ArrayPlot.of(_wgs, colorDataGradient));
        graphics.drawImage(bufferedImage, //
            0, 32, //
            bufferedImage.getWidth() * 2, bufferedImage.getHeight() * 2, null);
      }
      // render grid lines functions
      if (jToggleButtonArrows.isSelected()) {
        graphics.setColor(Color.LIGHT_GRAY);
        Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(Math.min(1, 3.0 / Math.sqrt(refinement()))));
        for (int i0 = 0; i0 < array.length; ++i0)
          for (int i1 = 0; i1 < array.length; ++i1) {
            Tensor mean = array[i0][i1];
            geometricLayer.pushMatrix(geodesicDisplay.matrixLift(mean));
            graphics.setColor(new Color(128, 128, 128, 64));
            graphics.fill(geometricLayer.toPath2D(shape, true));
            geometricLayer.popMatrix();
          }
      }
    }
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new R2ScatteredSetCoordinateDemo().setVisible(1200, 900);
  }
}
