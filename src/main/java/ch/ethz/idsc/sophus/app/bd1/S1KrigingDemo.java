// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.hs.sn.SnManifold;
import ch.ethz.idsc.sophus.krg.Kriging;
import ch.ethz.idsc.sophus.krg.PowerVariogram;
import ch.ethz.idsc.sophus.lie.so2.AngleVector;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ class S1KrigingDemo extends A1KrigingDemo {
  private static final Tensor CIRCLE = CirclePoints.of(61).map(N.DOUBLE);

  public S1KrigingDemo() {
    setControlPointsSe2(Tensors.fromString("{{1, 0, 0}, {0, 1.2, 0}, {-1, 1, 0}}"));
    timerFrame.configCoordinateOffset(500, 500);
    timerFrame.geometricComponent.addRenderInterfaceBackground(S1FrameRender.INSTANCE);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    Tensor control = getGeodesicControlPoints();
    final Tensor shape = getControlPointShape(); // .multiply(RealScalar.of(0.3));
    if (1 < control.length()) {
      // TODO check for zero norm below
      Tensor sequence = Tensor.of(control.stream().map(Normalize.with(Norm._2)));
      Tensor target = sequence;
      graphics.setColor(Color.GREEN);
      for (int index = 0; index < target.length(); ++index)
        graphics.draw(geometricLayer.toLine2D(control.get(index), target.get(index)));
      new PointsRender(new Color(64, 128, 64, 64), new Color(64, 128, 64, 255))
          // new PointsRender(new Color(128, 255, 128, 64), new Color(128, 255, 128, 255)) //
          .show(geodesicDisplay()::matrixLift, shape, target) //
          .render(geometricLayer, graphics);
      // ---
      Tensor values = Tensor.of(control.stream().map(Norm._2::ofVector));
      Tensor domain = Subdivide.of(Pi.VALUE.negate(), Pi.VALUE, 511);
      Tensor spherics = domain.map(AngleVector::of);
      // ---
      ScalarUnaryOperator variogram = PowerVariogram.of(RealScalar.ONE, spinnerBeta.getValue());
      Tensor covariance = DiagonalMatrix.with(ConstantArray.of(spinnerCvar.getValue(), sequence.length()));
      Kriging kriging = spinnerKriging.getValue().regression( //
          SnManifold.INSTANCE, variogram, sequence, values, covariance);
      Tensor curve = Tensor.of(CIRCLE.stream().map(kriging::estimate)).pmul(CIRCLE);
      // System.out.println(Pretty.of(curve.map(Round._3)));
      // kriging.estimate(point);
      // WeightingInterface weightingInterface = spinnerBarycentric.getValue().get();
      // try {
      // ScalarTensorFunction scalarTensorFunction = //
      // point -> weightingInterface.weights(sequence, AngleVector.of(point));
      // Tensor basis = Tensor.of(domain.stream().parallel().map(Scalar.class::cast).map(scalarTensorFunction));
      // Tensor curve = basis.dot(values).pmul(spherics);
      new PathRender(Color.BLUE, 1.25f).setCurve(curve, true).render(geometricLayer, graphics);
      // // ---
      // Reverse.of(spherics).stream().forEach(curve::append);
      // graphics.setColor(new Color(0, 0, 255, 32));
      // graphics.fill(geometricLayer.toPath2D(curve));
      // } catch (Exception exception) {
      // // ---
      // }
    }
    renderControlPoints(geometricLayer, graphics);
    // POINTS_RENDER_0.show(geodesicDisplay()::matrixLift, shape, getGeodesicControlPoints()).render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new S1KrigingDemo().setVisible(1000, 800);
  }
}
