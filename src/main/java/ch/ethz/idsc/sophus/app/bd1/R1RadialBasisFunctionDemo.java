// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.R2Display;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.itp.CrossAveraging;
import ch.ethz.idsc.sophus.itp.RadialBasisFunctionInterpolation;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

/** TODO investigate, this produces some nice results for kriging+metric+power */
/* package */ class R1RadialBasisFunctionDemo extends A1AveragingDemo {
  public R1RadialBasisFunctionDemo() {
    super(R2Display.INSTANCE);
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1, 2, 0}, {2, -1, 0}}"));
  }

  @Override
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    // ---
    RenderQuality.setQuality(graphics);
    Tensor control = Sort.of(getControlPointsSe2());
    if (1 < control.length()) {
      Tensor support = control.get(Tensor.ALL, 0);
      Tensor funceva = control.get(Tensor.ALL, 1);
      // ---
      Tensor sequence = support.map(Tensors::of);
      Tensor domain = domain();
      try {
        TensorUnaryOperator tensorUnaryOperator = //
            RadialBasisFunctionInterpolation.of(operator(sequence), sequence, funceva);
        Tensor result = Tensor.of(domain.stream().map(Tensors::of).map(tensorUnaryOperator));
        new PathRender(Color.BLUE, 1.25f) //
            .setCurve(Transpose.of(Tensors.of(domain, result)), false) //
            .render(geometricLayer, graphics);
      } catch (Exception exception) {
        // ---
      }
      if (!isDeterminate())
        try {
          TensorUnaryOperator weightingInterface = operator(sequence);
          TensorUnaryOperator operator = //
              CrossAveraging.of(p -> weightingInterface.apply(p), RnBiinvariantMean.INSTANCE, funceva);
          Tensor result = Tensor.of(domain.stream().map(Tensors::of).map(operator));
          new PathRender(Color.RED, 1.25f) //
              .setCurve(Transpose.of(Tensors.of(domain, result)), false) //
              .render(geometricLayer, graphics);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    }
  }

  public static void main(String[] args) {
    new R1RadialBasisFunctionDemo().setVisible(1000, 800);
  }
}
