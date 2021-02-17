// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.R2Display;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.itp.Kriging;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.sca.Abs;

// TODO behaves counter intuitively!?
/* package */ class R1KrigingDemo extends A1AveragingDemo {
  public R1KrigingDemo() {
    super(R2Display.INSTANCE);
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1, 1, 1}, {2, 2, 0}}"));
    // ---
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor control = Sort.of(getControlPointsSe2());
    if (1 < control.length()) {
      Tensor support = control.get(Tensor.ALL, 0);
      Tensor funceva = control.get(Tensor.ALL, 1);
      Tensor cvarian = control.get(Tensor.ALL, 2).multiply(RationalScalar.HALF).map(Abs.FUNCTION);
      // ---
      graphics.setColor(new Color(0, 128, 128));
      Scalar IND = RealScalar.of(0.1);
      for (int index = 0; index < support.length(); ++index) {
        geometricLayer.pushMatrix(Se2Matrix.translation(control.get(index)));
        Scalar v = cvarian.Get(index);
        graphics.draw(geometricLayer.toLine2D(Tensors.of(v.zero(), v), Tensors.of(v.zero(), v.negate())));
        graphics.draw(geometricLayer.toLine2D(Tensors.of(IND, v), Tensors.of(IND.negate(), v)));
        graphics.draw(geometricLayer.toLine2D(Tensors.of(IND, v.negate()), Tensors.of(IND.negate(), v.negate())));
        geometricLayer.popMatrix();
      }
      // ---
      Tensor sequence = support.map(Tensors::of);
      Tensor covariance = DiagonalMatrix.with(cvarian);
      TensorUnaryOperator weightingInterface = operator(sequence);
      try {
        Kriging kriging = Kriging.regression(weightingInterface, sequence, funceva, covariance);
        // ---
        Tensor domain = domain();
        Tensor result = Tensor.of(domain.stream().map(Tensors::of).map(kriging::estimate));
        Tensor errors = Tensor.of(domain.stream().map(Tensors::of).map(kriging::variance));
        // ---
        Path2D path2d = geometricLayer.toPath2D(Join.of( //
            Transpose.of(Tensors.of(domain, result.add(errors))), //
            Reverse.of(Transpose.of(Tensors.of(domain, result.subtract(errors))))));
        graphics.setColor(new Color(128, 128, 128, 32));
        graphics.fill(path2d);
        new PathRender(Color.BLUE, 1.25f) //
            .setCurve(Transpose.of(Tensors.of(domain, result)), false) //
            .render(geometricLayer, graphics);
      } catch (Exception exception) {
        // ---
      }
    }
  }

  public static void main(String[] args) {
    new R1KrigingDemo().setVisible(1000, 800);
  }
}
