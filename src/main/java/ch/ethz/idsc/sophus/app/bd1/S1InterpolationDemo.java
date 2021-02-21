// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.lev.LogWeightingDemo;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.gui.ren.PointsRender;
import ch.ethz.idsc.sophus.opt.LogWeightings;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.lie.r2.AngleVector;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.num.Pi;

/* package */ class S1InterpolationDemo extends LogWeightingDemo {
  public S1InterpolationDemo() {
    super(true, GeodesicDisplays.R2_ONLY, LogWeightings.list());
    setMidpointIndicated(false);
    // ---
    setControlPointsSe2(Tensors.fromString("{{1, 0, 0}, {0, 1.2, 0}, {-1, 0, 0}}"));
    timerFrame.geometricComponent.setOffset(500, 500);
    timerFrame.geometricComponent.addRenderInterfaceBackground(S1FrameRender.INSTANCE);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor control = getGeodesicControlPoints();
    final Tensor shape = getControlPointShape(); // .multiply(RealScalar.of(0.3));
    if (0 < control.length()) {
      // TODO check for zero norm below
      Tensor sequence = Tensor.of(control.stream().map(Vector2Norm.NORMALIZE));
      Tensor target = sequence;
      graphics.setColor(Color.GREEN);
      for (int index = 0; index < target.length(); ++index)
        graphics.draw(geometricLayer.toLine2D(control.get(index), target.get(index)));
      new PointsRender(new Color(64, 128, 64, 64), new Color(64, 128, 64, 255))
          // new PointsRender(new Color(128, 255, 128, 64), new Color(128, 255, 128, 255)) //
          .show(manifoldDisplay()::matrixLift, shape, target) //
          .render(geometricLayer, graphics);
      // ---
      Tensor values = Tensor.of(control.stream().map(Vector2Norm::of));
      Tensor domain = Subdivide.of(Pi.VALUE.negate(), Pi.VALUE, 511);
      Tensor spherics = domain.map(AngleVector::of);
      // ---
      TensorUnaryOperator tensorUnaryOperator = operator(sequence);
      try {
        ScalarTensorFunction scalarTensorFunction = //
            point -> tensorUnaryOperator.apply(AngleVector.of(point));
        Tensor basis = Tensor.of(domain.stream().parallel().map(Scalar.class::cast).map(scalarTensorFunction));
        Tensor curve = basis.dot(values).pmul(spherics);
        new PathRender(Color.BLUE, 1.25f).setCurve(curve, true).render(geometricLayer, graphics);
        // ---
        Reverse.of(spherics).stream().forEach(curve::append);
        graphics.setColor(new Color(0, 0, 255, 32));
        graphics.fill(geometricLayer.toPath2D(curve));
      } catch (Exception exception) {
        // ---
      }
    }
    renderControlPoints(geometricLayer, graphics);
    // POINTS_RENDER_0.show(geodesicDisplay()::matrixLift, shape, getGeodesicControlPoints()).render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new S1InterpolationDemo().setVisible(1000, 800);
  }
}
