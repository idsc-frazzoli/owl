// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ArrayRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
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
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.red.Entrywise;

// TODO redundancy with R2BarycentricCoordinatesDemo
/* package */ class R2ScatteredSetCoordinateDemo extends ControlPointsDemo {
  private final SpinnerLabel<RnBarycentricCoordinates> spinnerBarycentric = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = new SpinnerLabel<>();
  private final JToggleButton jToggleButton = new JToggleButton("heatmap");
  private final JToggleButton jToggleButtonAxes = new JToggleButton("axes");

  public R2ScatteredSetCoordinateDemo() {
    super(true, GeodesicDisplays.SE2C_SPD2_S2_R2);
    {
      spinnerBarycentric.setArray(RnBarycentricCoordinates.SCATTERED);
      spinnerBarycentric.setIndex(0);
      spinnerBarycentric.addToComponentReduced(timerFrame.jToolBar, new Dimension(170, 28), "barycentric");
    }
    {
      spinnerRefine.setList(Arrays.asList(3, 5, 10, 15, 20, 25, 30, 35, 40));
      spinnerRefine.setValue(15);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "refinement");
    }
    timerFrame.jToolBar.addSeparator();
    {
      timerFrame.jToolBar.add(jToggleButton);
    }
    timerFrame.jToolBar.addSeparator();
    {
      spinnerColorData.setArray(ColorDataGradients.values());
      spinnerColorData.setIndex(0);
      spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "color scheme");
    }
    {
      jToggleButtonAxes.setSelected(true);
      timerFrame.jToolBar.add(jToggleButtonAxes);
    }
    setControlPointsSe2(Tensors.fromString("{{2, -3, 1.5}, {3, 5, 1}, {-4, -3, 1}, {-5, 3, 2}}"));
    timerFrame.configCoordinateOffset(500, 500);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleButtonAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor controlPoints = getGeodesicControlPoints();
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    if (2 < controlPoints.length()) {
      Tensor domain = Tensor.of(controlPoints.stream().map(geodesicDisplay::toPoint));
      GraphicsUtil.setQualityHigh(graphics);
      BarycentricCoordinate barycentricCoordinate = spinnerBarycentric.getValue().barycentricCoordinate();
      Tensor min = Entrywise.min().of(domain).map(RealScalar.of(0.01)::add);
      Tensor max = Entrywise.max().of(domain).map(RealScalar.of(0.01)::subtract).negate();
      min = Tensors.vector(-5, -5);
      max = Tensors.vector(+5, +5);
      Tensor sX = Subdivide.of(min.Get(0), max.Get(0), spinnerRefine.getValue());
      Tensor sY = Subdivide.of(min.Get(1), max.Get(1), spinnerRefine.getValue());
      Tensor[][] array = new Tensor[sX.length()][sY.length()];
      Tensor[][] point = new Tensor[sX.length()][sY.length()];
      Tensor wgs = Array.of(l -> DoubleScalar.INDETERMINATE, sX.length(), sY.length(), domain.length());
      int c0 = 0;
      for (Tensor x : sX) {
        int c1 = 0;
        for (Tensor y : sY) {
          Tensor px = Tensors.of(x, y);
          Tensor weights = barycentricCoordinate.weights(domain, px);
          wgs.set(weights, c0, c1);
          Tensor mean = biinvariantMean.mean(controlPoints, weights);
          array[c0][c1] = mean;
          point[c0][c1] = geodesicDisplay.toPoint(mean);
          ++c1;
        }
        ++c0;
      }
      if (jToggleButton.isSelected()) { // render basis functions
        int pix = 0;
        for (int basis = 0; basis < domain.length(); ++basis) {
          Tensor image = ArrayPlot.of(wgs.get(Tensor.ALL, Tensor.ALL, basis), ColorDataGradients.CLASSIC);
          BufferedImage bufferedImage = ImageFormat.of(image);
          int wid = bufferedImage.getWidth() * 4;
          graphics.drawImage(bufferedImage, pix, 32, wid, bufferedImage.getHeight() * 4, null);
          pix += wid;
        }
      }
      // render grid lines functions
      graphics.setColor(Color.LIGHT_GRAY);
      ColorDataGradient colorDataGradient = spinnerColorData.getValue().deriveWithOpacity(RationalScalar.HALF);
      new ArrayRender(point, colorDataGradient).render(geometricLayer, graphics);
      Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(Math.min(1, 3.0 / Math.sqrt(spinnerRefine.getValue()))));
      for (int i0 = 0; i0 < array.length; ++i0)
        for (int i1 = 0; i1 < array.length; ++i1) {
          Tensor mean = array[i0][i1];
          geometricLayer.pushMatrix(geodesicDisplay.matrixLift(mean));
          graphics.setColor(new Color(128, 128, 128, 64));
          graphics.fill(geometricLayer.toPath2D(shape, true));
          geometricLayer.popMatrix();
        }
    }
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new R2ScatteredSetCoordinateDemo().setVisible(1200, 900);
  }
}
