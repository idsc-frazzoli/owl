// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.BSplineFunction;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

public class BSplineFunctionDemo extends CurvatureDemo {
  private static final List<Integer> DEGREES = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
  private final SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();
  private final JToggleButton jToggleButton = new JToggleButton("cyclic");

  public BSplineFunctionDemo() {
    super(GeodesicDisplays.R2_ONLY);
    // ---
    spinnerDegree.setList(DEGREES);
    spinnerDegree.setValue(3);
    spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "degree");
    // ---
    timerFrame.jToolBar.add(jToggleButton);
  }

  @Override
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // ---
    GraphicsUtil.setQualityHigh(graphics);
    renderControlPoints(geometricLayer, graphics); // control points
    Tensor control = getGeodesicControlPoints();
    Tensor refined = Tensors.empty();
    if (0 < control.length()) {
      int degree = spinnerDegree.getValue();
      boolean cyclic = jToggleButton.isSelected();
      ScalarTensorFunction scalarTensorFunction = cyclic //
          ? BSplineFunction.cyclic(degree, control)
          : BSplineFunction.string(degree, control);
      refined = Subdivide.of(0, cyclic ? control.length() : control.length() - 1, 100) //
          .map(scalarTensorFunction);
      new PathRender(Color.BLUE).setCurve(refined, cyclic).render(geometricLayer, graphics);
    }
    return refined;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new BSplineFunctionDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
