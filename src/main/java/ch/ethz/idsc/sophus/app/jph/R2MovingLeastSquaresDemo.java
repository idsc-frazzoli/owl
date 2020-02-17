// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.stream.IntStream;

import javax.swing.JButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ArrayRender;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PointsRender;
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.RnBarycentricCoordinates;
import ch.ethz.idsc.sophus.app.api.RnMotionFits;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ class R2MovingLeastSquaresDemo extends ScatteredSetCoordinateDemo {
  private static final Tensor ORIGIN = CirclePoints.of(3).multiply(RealScalar.of(0.2));
  private static final PointsRender POINTS_RENDER_POINTS = //
      new PointsRender(new Color(64, 255, 64, 64), new Color(64, 255, 64, 255));
  // ---
  private final SpinnerLabel<RnMotionFits> spinnerRnMotionFits = new SpinnerLabel<>();
  //
  private final SpinnerLabel<Integer> spinnerLength = new SpinnerLabel<>();
  private final JButton jButton = new JButton("snap");
  private Tensor points;

  R2MovingLeastSquaresDemo() {
    super(false, GeodesicDisplays.R2_ONLY, RnBarycentricCoordinates.SCATTERED);
    setMidpointIndicated(false);
    // ---
    {
      spinnerRnMotionFits.setArray(RnMotionFits.values());
      spinnerRnMotionFits.setIndex(0);
      spinnerRnMotionFits.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "motion fits");
    }
    {
      spinnerLength.addSpinnerListener(this::shufflePoints);
      spinnerLength.setList(Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10));
      spinnerLength.setValue(4);
      spinnerLength.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "number of points");
    }
    {
      jButton.addActionListener(l -> points = getGeodesicControlPoints());
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
      BarycentricCoordinate barycentricCoordinate = barycentricCoordinate();
      int n = refinement();
      Tensor dx = Subdivide.of(0, 6, n - 1);
      Tensor dy = Subdivide.of(0, 6, n - 1);
      Tensor[][] array = new Tensor[dx.length()][dy.length()];
      RnMotionFits rnMotionFits = spinnerRnMotionFits.getValue();
      IntStream.range(0, n).parallel().forEach(cx -> {
        for (int cy = 0; cy < n; ++cy) {
          Tensor p = Tensors.of(dx.get(cx), dy.get(cy));
          Tensor weights = barycentricCoordinate.weights(points, p);
          array[cx][cy] = rnMotionFits.map(points, target, weights, p);
        }
      });
      new ArrayRender(array, colorDataGradient().deriveWithOpacity(RationalScalar.HALF)) //
          .render(geometricLayer, graphics);
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
