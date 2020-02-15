// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ArrayRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PointsRender;
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.RnBarycentricCoordinates;
import ch.ethz.idsc.sophus.app.api.RnMotionFits;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ class R2MovingLeastSquaresDemo extends ControlPointsDemo {
  private static final Tensor ORIGIN = CirclePoints.of(3).multiply(RealScalar.of(.2));
  private static final PointsRender POINTS_RENDER_POINTS = //
      new PointsRender(new Color(64, 255, 64, 64), new Color(64, 255, 64, 255));
  // ---
  private final SpinnerLabel<RnBarycentricCoordinates> spinnerRnPointWeights = new SpinnerLabel<>();
  private final SpinnerLabel<RnMotionFits> spinnerRnMotionFits = new SpinnerLabel<>();
  //
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerLength = new SpinnerLabel<>();
  private final JButton jButton = new JButton("snap");
  private Tensor points;

  R2MovingLeastSquaresDemo() {
    super(false, GeodesicDisplays.R2_ONLY);
    setMidpointIndicated(false);
    // ---
    {
      spinnerRnPointWeights.setArray(RnBarycentricCoordinates.SCATTERED);
      spinnerRnPointWeights.setIndex(0);
      spinnerRnPointWeights.addToComponentReduced(timerFrame.jToolBar, new Dimension(280, 28), "point weights");
    }
    {
      spinnerRnMotionFits.setArray(RnMotionFits.values());
      spinnerRnMotionFits.setIndex(0);
      spinnerRnMotionFits.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "motion fits");
    }
    {
      spinnerRefine.setList(Arrays.asList(10, 15, 20, 25, 30, 35, 40));
      spinnerRefine.setIndex(1);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "refinement");
    }
    {
      spinnerLength.addSpinnerListener(this::shufflePoints);
      spinnerLength.setList(Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10));
      spinnerLength.setValue(4);
      spinnerLength.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "number of points");
    }
    {
      jButton.addActionListener(l -> points = Tensor.of(getControlPointsSe2().stream().map(Extract2D.FUNCTION)));
      timerFrame.jToolBar.add(jButton);
    }
    shufflePoints(spinnerLength.getValue());
    timerFrame.configCoordinateOffset(300, 500);
  }

  private synchronized void shufflePoints(int n) {
    Distribution distribution = UniformDistribution.of(-1, 7);
    points = RandomVariate.of(distribution, n, 2);
    setControlPointsSe2(Tensor.of(points.stream() //
        .map(Tensor::copy) //
        .map(row -> row.append(RealScalar.ZERO))));
  }

  @Override // from RenderInterface
  public synchronized void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    {
      Tensor target = Tensor.of(getGeodesicControlPoints().stream().map(R2GeodesicDisplay.INSTANCE::project));
      graphics.setColor(Color.BLUE);
      BarycentricCoordinate barycentricCoordinate = spinnerRnPointWeights.getValue().get();
      int n = spinnerRefine.getValue();
      Tensor dx = Subdivide.of(0, 6, n - 1);
      Tensor dy = Subdivide.of(0, 6, n - 1);
      Tensor[][] array = new Tensor[dx.length()][dy.length()];
      RnMotionFits rnMotionFits = spinnerRnMotionFits.getValue();
      for (int cx = 0; cx < n; ++cx)
        for (int cy = 0; cy < n; ++cy) {
          Tensor p = Tensors.of(dx.get(cx), dy.get(cy));
          Tensor weights = barycentricCoordinate.weights(points, p);
          array[cx][cy] = rnMotionFits.map(points, target, weights, p);
        }
      ColorDataGradient colorDataGradient = ColorDataGradients.PARULA.deriveWithOpacity(RationalScalar.HALF);
      new ArrayRender(array, colorDataGradient).render(geometricLayer, graphics);
      graphics.setColor(Color.RED);
      for (int index = 0; index < points.length(); ++index)
        graphics.draw(geometricLayer.toPath2D(Tensors.of(points.get(index), target.get(index))));
    }
    renderControlPoints(geometricLayer, graphics);
    POINTS_RENDER_POINTS //
        .show(R2GeodesicDisplay.INSTANCE::matrixLift, ORIGIN, points) //
        .render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new R2MovingLeastSquaresDemo().setVisible(1000, 800);
  }
}
