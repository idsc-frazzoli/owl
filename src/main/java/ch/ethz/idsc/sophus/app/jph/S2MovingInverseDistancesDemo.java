// code by jph
package ch.ethz.idsc.sophus.app.jph;

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
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PointsRender;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.SnBarycentricCoordinates;
import ch.ethz.idsc.sophus.app.api.SnMeans;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.N;

/* package */ class S2MovingInverseDistancesDemo extends ScatteredSetCoordinateDemo {
  private static final Tensor TRIANGLE = CirclePoints.of(3).multiply(RealScalar.of(0.05));
  private static final PointsRender POINTS_RENDER_POINTS = //
      new PointsRender(new Color(64, 255, 64, 64), new Color(64, 255, 64, 255));
  // ---
  private final SpinnerLabel<SnMeans> spinnerMotionFits = new SpinnerLabel<>();
  // ---
  private final SpinnerLabel<Integer> spinnerLength = new SpinnerLabel<>();
  private final JButton jButton = new JButton("snap");
  // ---
  private Tensor originSe2;

  S2MovingInverseDistancesDemo() {
    super(false, GeodesicDisplays.S2_ONLY, SnBarycentricCoordinates.values());
    setMidpointIndicated(false);
    // ---
    {
      spinnerMotionFits.setArray(SnMeans.values());
      spinnerMotionFits.setIndex(0);
      spinnerMotionFits.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "motion fits");
    }
    {
      spinnerLength.addSpinnerListener(this::shufflePoints);
      spinnerLength.setList(Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10));
      spinnerLength.setValue(5);
      spinnerLength.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "number of points");
    }
    {
      jButton.addActionListener(l -> updateOrigin(getControlPointsSe2().map(N.DOUBLE)));
      timerFrame.jToolBar.add(jButton);
    }
    refinement(10);
    shufflePoints(spinnerLength.getValue());
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    timerFrame.configCoordinateOffset(400, 400);
  }

  private MovingDomain2D movingDomain2D;

  private synchronized void shufflePoints(int n) {
    Distribution distribution = UniformDistribution.of(-0.5, 0.5);
    originSe2 = Tensor.of(RandomVariate.of(distribution, n, 2).stream() //
        .map(Tensor::copy) //
        .map(row -> row.append(RealScalar.ZERO)));
    updateOrigin(originSe2);
  }

  private void updateOrigin(Tensor originSe2) {
    this.originSe2 = originSe2;
    setControlPointsSe2(originSe2);
    S2GeodesicDisplay s2geodesicDisplay = (S2GeodesicDisplay) geodesicDisplay();
    Tensor origin = Tensor.of(originSe2.stream().map(s2geodesicDisplay::project));
    int res = refinement();
    Tensor dx = Subdivide.of(-1, 1, res - 1);
    Tensor dy = Subdivide.of(-1, 1, res - 1);
    Tensor domain = Tensors.matrix((cx, cy) -> NORMALIZE.apply(Tensors.of(dx.get(cx), dy.get(cy), RealScalar.of(2))), dx.length(), dy.length());
    movingDomain2D = new MovingDomain2D(origin, barycentricCoordinate(), domain);
  }

  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);

  @Override // from RenderInterface
  public synchronized void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    S2GeodesicDisplay s2geodesicDisplay = (S2GeodesicDisplay) geodesicDisplay();
    Tensor origin = movingDomain2D.origin();
    Tensor target = getGeodesicControlPoints();
    Tensor[][] array = movingDomain2D.forward(target, spinnerMotionFits.getValue().get());
    new ArrayRender(array, colorDataGradient().deriveWithOpacity(RationalScalar.HALF)) //
        .render(geometricLayer, graphics);
    graphics.setColor(Color.RED);
    for (int index = 0; index < origin.length(); ++index)
      graphics.draw(geometricLayer.toPath2D(Tensors.of( //
          origin.get(index), //
          target.get(index))));
    POINTS_RENDER_POINTS //
        .show(s2geodesicDisplay::matrixLift, TRIANGLE, movingDomain2D.origin()) //
        .render(geometricLayer, graphics);
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new S2MovingInverseDistancesDemo().setVisible(1000, 800);
  }
}
