package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.krg.PowerVariogram;
import ch.ethz.idsc.sophus.krg.PseudoDistances;
import ch.ethz.idsc.sophus.krg.RadialBasisFunctionInterpolation;
import ch.ethz.idsc.sophus.lie.rn.RnManifold;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Abs;

/* package */ class R1RadialBasisFunctionDemo extends ControlPointsDemo {
  public R1RadialBasisFunctionDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    // ---
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
      TensorUnaryOperator tensorUnaryOperator = //
          RadialBasisFunctionInterpolation.normalized( //
              PseudoDistances.RELATIVE.of(RnManifold.INSTANCE, PowerVariogram.of(1, 1.5)), sequence, funceva);
      // ---
      Tensor domain = Subdivide.of(-5, 5, 100);
      Tensor result = Tensor.of(domain.stream().map(Tensors::of).map(tensorUnaryOperator));
      // ---
      new PathRender(Color.BLUE, 1.25f) //
          .setCurve(Transpose.of(Tensors.of(domain, result)), false) //
          .render(geometricLayer, graphics);
    }
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new R1RadialBasisFunctionDemo().setVisible(1000, 800);
  }
}
