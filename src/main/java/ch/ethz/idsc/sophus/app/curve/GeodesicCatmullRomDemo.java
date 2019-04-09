// code by ob / jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;

import javax.swing.JSlider;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.DubinsGenerator;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.misc.CurveCurvatureRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.curve.GeodesicCatmullRom;
import ch.ethz.idsc.sophus.math.CentripedalKnotSpacing;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

public class GeodesicCatmullRomDemo extends CurveDemo {
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleSymi = new JToggleButton("graph");
  private final JSlider jSlider = new JSlider(0, 1000, 500);
  private final JSlider jSliderAlpha = new JSlider(0, 1000, 500);

  public GeodesicCatmullRomDemo() {
    addButtonDubins();
    // ---
    geodesicDisplaySpinner.setValue(Se2GeodesicDisplay.INSTANCE);
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20));
    spinnerRefine.setValue(5);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    jToggleSymi.setSelected(true);
    timerFrame.jToolBar.add(jToggleSymi);
    // ---
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
    // ---
    jSliderAlpha.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSliderAlpha);
    {
      Tensor dubins = Tensors.fromString("{{1,1,0}, {1,2,-1}, {2,1,0.5}}");
      setControl(DubinsGenerator.of(Tensors.vector(0, 0, 0), //
          Tensor.of(dubins.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))))));
    }
  }

  @Override // from RenderInterface
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final int levels = spinnerRefine.getValue();
    final Tensor control = control();
    GraphicsUtil.setQualityHigh(graphics);
    renderControlPoints(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    CentripedalKnotSpacing centripedalKnotSpacing = new CentripedalKnotSpacing(RationalScalar.of(jSliderAlpha.getValue(), jSliderAlpha.getMaximum()),
        geodesicDisplay::parametricDifference);
    Tensor knots = centripedalKnotSpacing.apply(control);
    final Scalar parameter = knots.Get(knots.length() - 2).subtract(knots.get(1)).multiply(RationalScalar.of(jSlider.getValue(), jSlider.getMaximum() + 1))
        .add(knots.get(1));
    ScalarTensorFunction scalarTensorFunction = GeodesicCatmullRom.of(geodesicInterface, knots, control);
    Tensor refined = Subdivide
        .of(knots.Get(1).number().floatValue(), knots.Get(knots.length() - 2).number().floatValue() - 0.000001, Math.max(1, levels * control.length()))
        .map(scalarTensorFunction);
    {
      Tensor selected = scalarTensorFunction.apply(parameter);
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(selected));
      Path2D path2d = geometricLayer.toPath2D(geodesicDisplay.shape());
      graphics.setColor(Color.DARK_GRAY);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
    CurveCurvatureRender.of(render, false, geometricLayer, graphics);
    return refined;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new GeodesicCatmullRomDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
