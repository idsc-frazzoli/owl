// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.lie.rn.RnInverseDistanceCoordinate;
import ch.ethz.idsc.sophus.math.win.AffineCoordinate;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class InverseDistanceInterpolationDemo extends ControlPointsDemo {
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleItrp = new JToggleButton("interp");

  InverseDistanceInterpolationDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    // ---
    timerFrame.jToolBar.add(jToggleItrp);
    jToggleItrp.setEnabled(false);
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
    spinnerRefine.setValue(4);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1, 0, 0}}"));
    // ---
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    int levels = spinnerRefine.getValue();
    Tensor control = getGeodesicControlPoints();
    Tensor support = control.get(Tensor.ALL, 0);
    Tensor funceva = control.get(Tensor.ALL, 1);
    Scalar min = (Scalar) support.stream().reduce(Min::of).get();
    Scalar max = (Scalar) support.stream().reduce(Max::of).get();
    // ---
    Clip clip = Clips.interval(min, max);
    Tensor domain = Subdivide.increasing(clip, 4 << levels);
    // ---
    // InverseDistanceWeighting
    BarycentricCoordinate idc = RnInverseDistanceCoordinate.SQUARED;
    renderControlPoints(geometricLayer, graphics);
    Tensor sequence = Tensor.of(support.stream().map(Tensors::of));
    ScalarTensorFunction scalarTensorFunction = //
        point -> idc.weights(sequence, Tensors.of(point));
    TensorUnaryOperator tensorUnaryOperator = AffineCoordinate.of(sequence);
    scalarTensorFunction = //
        point -> tensorUnaryOperator.apply(Tensors.of(point));
    Tensor values = domain.map(scalarTensorFunction);
    Tensor curve = Transpose.of(Tensors.of(domain, values.dot(funceva)));
    new PathRender(Color.BLUE, 1.25f).setCurve(curve, false).render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new InverseDistanceInterpolationDemo().setVisible(1000, 800);
  }
}
