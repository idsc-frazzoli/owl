// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PointsRender;
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.opt.RigidMotionFit;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.ArcTan;

/* package */ class RigidMotionFitDemo extends ControlPointsDemo {
  private static final Tensor ORIGIN = CirclePoints.of(3).multiply(RealScalar.of(.2));
  private static final PointsRender POINTS_RENDER_RESULT = //
      new PointsRender(new Color(128, 128, 255, 64), new Color(128, 128, 255, 255));
  private static final PointsRender POINTS_RENDER_POINTS = //
      new PointsRender(new Color(64, 255, 64, 64), new Color(64, 255, 64, 255));
  // ---
  private final SpinnerLabel<Integer> spinnerLength = new SpinnerLabel<>();
  private Tensor points;

  RigidMotionFitDemo() {
    super(false, GeodesicDisplays.R2_ONLY);
    setMidpointIndicated(false);
    shufflePoints(5);
    // ---
    spinnerLength.addSpinnerListener(this::shufflePoints);
    spinnerLength.setList(Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10));
    spinnerLength.setValue(4);
    spinnerLength.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
  }

  private void shufflePoints(int n) {
    Distribution distribution = NormalDistribution.of(0, 2);
    points = RandomVariate.of(distribution, n, 3);
    setControlPointsSe2(points);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    {
      Tensor target = getGeodesicControlPoints();
      Tensor pxy = Tensor.of(points.stream().map(R2GeodesicDisplay.INSTANCE::project));
      Tensor cxy = Tensor.of(target.stream().map(R2GeodesicDisplay.INSTANCE::project));
      RigidMotionFit rigidMotionFit = RigidMotionFit.of(pxy, cxy);
      Tensor rotation = rigidMotionFit.rotation(); // 2 x 2
      Scalar angle = ArcTan.of(rotation.Get(0, 0), rotation.Get(1, 0));
      Tensor mouse = rigidMotionFit.translation().append(angle);
      POINTS_RENDER_RESULT //
          .show(Se2GeodesicDisplay.INSTANCE::matrixLift, Se2GeodesicDisplay.INSTANCE.shape(), Tensors.of(mouse)) //
          .render(geometricLayer, graphics);
      {
        graphics.setColor(Color.RED);
        for (int index = 0; index < points.length(); ++index)
          graphics.draw(geometricLayer.toPath2D(Tensors.of(points.get(index), target.get(index))));
      }
    }
    renderControlPoints(geometricLayer, graphics);
    POINTS_RENDER_POINTS.show(R2GeodesicDisplay.INSTANCE::matrixLift, ORIGIN, points).render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new RigidMotionFitDemo().setVisible(1000, 800);
  }
}
