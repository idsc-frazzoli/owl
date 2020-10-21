// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.lev.LogWeightingDemo;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

/* package */ abstract class A1BarycentricCoordinateDemo extends LogWeightingDemo {
  public A1BarycentricCoordinateDemo(List<LogWeighting> array) {
    super(true, GeodesicDisplays.R2_ONLY, array);
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1, 1, 0}, {2, 2, 0}}"));
    // ---
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override // from RenderInterface
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor control = getGeodesicControlPoints();
    if (1 < control.length()) {
      Tensor support = control.get(Tensor.ALL, 0);
      Tensor funceva = control.get(Tensor.ALL, 1);
      // ---
      Tensor domain = domain(support);
      // ---
      Tensor sequence = support.map(this::lift);
      TensorUnaryOperator weightingInterface = operator(sequence);
      ScalarTensorFunction scalarTensorFunction = //
          point -> weightingInterface.apply(lift(point));
      Tensor basis = domain.map(scalarTensorFunction);
      {
        Tensor curve = Transpose.of(Tensors.of(domain, basis.dot(funceva)));
        new PathRender(Color.BLUE, 1.25f).setCurve(curve, false).render(geometricLayer, graphics);
      }
      ColorDataIndexed colorDataIndexed = ColorDataLists._097.cyclic();
      for (int index = 0; index < funceva.length(); ++index) {
        Color color = colorDataIndexed.getColor(index);
        Tensor curve = Transpose.of(Tensors.of(domain, basis.get(Tensor.ALL, index)));
        new PathRender(color, 1f).setCurve(curve, false).render(geometricLayer, graphics);
      }
    }
  }

  abstract Tensor domain(Tensor support);

  abstract Tensor lift(Scalar x);
}
