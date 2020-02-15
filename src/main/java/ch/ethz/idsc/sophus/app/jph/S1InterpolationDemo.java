// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.function.Supplier;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.api.PointsRender;
import ch.ethz.idsc.sophus.app.api.SnBarycentricCoordinates;
import ch.ethz.idsc.sophus.lie.so2.AngleVector;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ class S1InterpolationDemo extends ControlPointsDemo {
  private final SpinnerLabel<Supplier<BarycentricCoordinate>> spinnerBarycentric = new SpinnerLabel<>();
  private static final Scalar RADIUS = RealScalar.of(3);
  private static final Tensor CIRCLE = CirclePoints.of(61).multiply(RADIUS);

  public S1InterpolationDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    {
      spinnerBarycentric.setArray(SnBarycentricCoordinates.values());
      spinnerBarycentric.setIndex(0);
      spinnerBarycentric.addToComponentReduced(timerFrame.jToolBar, new Dimension(170, 28), "barycentric");
    }
    // ---
    setControlPointsSe2(Tensors.fromString("{{1, 0, 0}, {0, 1.2, 0}, {-1, 0, 0}}").multiply(RADIUS));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    {
      graphics.setColor(Color.LIGHT_GRAY);
      graphics.draw(geometricLayer.toPath2D(CIRCLE, true));
    }
    Tensor control = getGeodesicControlPoints();
    if (1 < control.length()) {
      Tensor sequence = Tensor.of(control.stream().map(Normalize.with(Norm._2)));
      Tensor target = sequence.multiply(RADIUS);
      graphics.setColor(Color.GREEN);
      for (int index = 0; index < target.length(); ++index) {
        graphics.draw(geometricLayer.toLine2D(control.get(index), target.get(index)));
      }
      new PointsRender(new Color(128, 255, 128, 64), new Color(128, 255, 128, 255)) //
          .show(geodesicDisplay()::matrixLift, getControlPointShape(), target) //
          .render(geometricLayer, graphics);
      // ---
      Tensor values = Tensor.of(control.stream().map(Norm._2::ofVector));
      // ---
      Tensor domain = Subdivide.of(Pi.VALUE.negate(), Pi.VALUE, 255);
      Tensor spherics = domain.map(AngleVector::of);
      // ---
      BarycentricCoordinate barycentricCoordinate = spinnerBarycentric.getValue().get();
      ScalarTensorFunction scalarTensorFunction = //
          point -> barycentricCoordinate.weights(sequence, AngleVector.of(point));
      Tensor basis = domain.map(scalarTensorFunction);
      Tensor curve = basis.dot(values).pmul(spherics);
      new PathRender(Color.BLUE, 1.25f).setCurve(curve, true).render(geometricLayer, graphics);
      // ---
      Reverse.of(spherics.multiply(RADIUS)).stream().forEach(curve::append);
      graphics.setColor(new Color(0, 0, 255, 32));
      graphics.fill(geometricLayer.toPath2D(curve));
    }
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new S1InterpolationDemo().setVisible(1000, 800);
  }
}
