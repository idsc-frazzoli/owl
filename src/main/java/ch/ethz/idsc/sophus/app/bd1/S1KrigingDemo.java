// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.R2Display;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.gui.ren.PointsRender;
import ch.ethz.idsc.sophus.hs.sn.SnManifold;
import ch.ethz.idsc.sophus.itp.Kriging;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.d2.ArcTan2D;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Drop;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.N;

// FIXME what does this demo do: there is no curve shown
/* package */ class S1KrigingDemo extends AnAveragingDemo {
  private static final Tensor DOMAIN = Drop.tail(CirclePoints.of(161).map(N.DOUBLE), 80);

  public S1KrigingDemo() {
    super(Arrays.asList(R2Display.INSTANCE));
    setControlPointsSe2(Tensors.fromString("{{1, 0, 0}, {0, 1.2, 0}, {-1, 1, 0}}"));
    timerFrame.geometricComponent.setOffset(500, 500);
    timerFrame.geometricComponent.addRenderInterfaceBackground(S1FrameRender.INSTANCE);
  }

  @Override // from RenderInterface
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor control = getGeodesicControlPoints();
    final Tensor shape = getControlPointShape(); // .multiply(RealScalar.of(0.3));
    if (1 < control.length()) {
      // TODO check for zero norm below
      Tensor sequence = Tensor.of(control.stream().map(Vector2Norm.NORMALIZE));
      Tensor funceva = Tensor.of(control.stream().map(Vector2Norm::of));
      Tensor cvarian = getControlPointsSe2().get(Tensor.ALL, 2).multiply(RationalScalar.HALF).map(Abs.FUNCTION);
      // ---
      graphics.setColor(new Color(0, 128, 128));
      Scalar IND = RealScalar.of(0.1);
      for (int index = 0; index < sequence.length(); ++index) {
        Tensor xy = control.get(index).copy();
        xy.append(ArcTan2D.of(xy).add(Pi.HALF));
        geometricLayer.pushMatrix(Se2Matrix.of(xy));
        Scalar v = cvarian.Get(index);
        graphics.draw(geometricLayer.toLine2D(Tensors.of(v.zero(), v), Tensors.of(v.zero(), v.negate())));
        graphics.draw(geometricLayer.toLine2D(Tensors.of(IND, v), Tensors.of(IND.negate(), v)));
        graphics.draw(geometricLayer.toLine2D(Tensors.of(IND, v.negate()), Tensors.of(IND.negate(), v.negate())));
        geometricLayer.popMatrix();
      }
      // ---
      graphics.setColor(Color.GREEN);
      for (int index = 0; index < sequence.length(); ++index)
        graphics.draw(geometricLayer.toLine2D(control.get(index), sequence.get(index)));
      new PointsRender(new Color(64, 128, 64, 64), new Color(64, 128, 64, 255)) //
          .show(manifoldDisplay()::matrixLift, shape, sequence) //
          .render(geometricLayer, graphics);
      Tensor covariance = DiagonalMatrix.with(cvarian);
      if (isDeterminate()) {
        TensorUnaryOperator tensorUnaryOperator = operator(SnManifold.INSTANCE, sequence);
        Kriging kriging = Kriging.regression(tensorUnaryOperator, sequence, funceva, covariance);
        Tensor estimate = Tensor.of(DOMAIN.stream().map(kriging::estimate));
        Tensor curve = estimate.pmul(DOMAIN);
        new PathRender(Color.BLUE, 1.25f).setCurve(curve, false).render(geometricLayer, graphics);
        Tensor errors = Tensor.of(DOMAIN.stream().map(kriging::variance));
        // ---
        Path2D path2d = geometricLayer.toPath2D(Join.of( //
            estimate.add(errors).pmul(DOMAIN), //
            Reverse.of(estimate.subtract(errors).pmul(DOMAIN))));
        graphics.setColor(new Color(128, 128, 128, 32));
        graphics.fill(path2d);
      }
    }
  }

  public static void main(String[] args) {
    new S1KrigingDemo().setVisible(1000, 800);
  }
}
