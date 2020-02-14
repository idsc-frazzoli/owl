// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.api.RnBarycentricCoordinates;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class R1BarycentricCoordinateDemo extends ControlPointsDemo {
  private final SpinnerLabel<RnBarycentricCoordinates> spinnerBarycentric = new SpinnerLabel<>();

  public R1BarycentricCoordinateDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    {
      spinnerBarycentric.setArray(RnBarycentricCoordinates.SCATTERED);
      spinnerBarycentric.setIndex(0);
      spinnerBarycentric.addToComponentReduced(timerFrame.jToolBar, new Dimension(170, 28), "barycentric");
    }
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1, 0, 0}}"));
    // ---
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor control = getGeodesicControlPoints();
    if (1 < control.length()) {
      Tensor support = control.get(Tensor.ALL, 0);
      Tensor funceva = control.get(Tensor.ALL, 1);
      Scalar min = (Scalar) support.stream().reduce(Min::of).get().subtract(RealScalar.ONE);
      Scalar max = (Scalar) support.stream().reduce(Max::of).get().add(RealScalar.ONE);
      // ---
      Clip clip = Clips.interval(min, max);
      Tensor domain = Subdivide.increasing(clip, 255);
      // ---
      BarycentricCoordinate barycentricCoordinate = spinnerBarycentric.getValue().barycentricCoordinate();
      Tensor sequence = Tensor.of(support.stream().map(Tensors::of));
      ScalarTensorFunction scalarTensorFunction = //
          point -> barycentricCoordinate.weights(sequence, Tensors.of(point));
      Tensor curve = Transpose.of(Tensors.of(domain, domain.map(scalarTensorFunction).dot(funceva)));
      new PathRender(Color.BLUE, 1.25f).setCurve(curve, false).render(geometricLayer, graphics);
    }
  }

  public static void main(String[] args) {
    new R1BarycentricCoordinateDemo().setVisible(1000, 800);
  }
}
