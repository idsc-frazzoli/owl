// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.swing.JSlider;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.DubinsGenerator;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.curve.AbstractBSplineInterpolation;
import ch.ethz.idsc.sophus.curve.AbstractBSplineInterpolation.Iteration;
import ch.ethz.idsc.sophus.curve.GeodesicBSplineFunction;
import ch.ethz.idsc.sophus.curve.GeodesicBSplineInterpolation;
import ch.ethz.idsc.sophus.curve.LieGroupBSplineInterpolation;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.sym.SymLinkImage;
import ch.ethz.idsc.sophus.sym.SymLinkImages;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.sca.Chop;

/* package */ class BSplineFunctionDemo extends ControlPointsDemo {
  private static final List<Integer> DEGREES = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
  // ---
  private final SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleItrp = new JToggleButton("interp");
  private final JToggleButton jToggleSymi = new JToggleButton("graph");
  private final JSlider jSlider = new JSlider(0, 1000, 500);

  BSplineFunctionDemo() {
    super(true, true, GeodesicDisplays.ALL);
    // ---
    addButtonDubins();
    // ---
    timerFrame.jToolBar.add(jToggleItrp);
    // ---
    spinnerDegree.setList(DEGREES);
    spinnerDegree.setValue(3);
    spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "degree");
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    spinnerRefine.setValue(5);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    jToggleSymi.setSelected(true);
    timerFrame.jToolBar.add(jToggleSymi);
    // ---
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
    {
      Tensor dubins = Tensors.fromString("{{1,0,0},{1,0,0},{2,0,2.5708},{1,0,2.1},{1.5,0,0},{2.3,0,-1.2},{1.5,0,0},{4,0,3.14159},{2,0,3.14159},{2,0,0}}");
      setControl(DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
          Tensor.of(dubins.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))))));
    }
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final int degree = spinnerDegree.getValue();
    final int levels = spinnerRefine.getValue();
    final Tensor control = control();
    final int upper = control.length() - 1;
    final Scalar parameter = RationalScalar.of(jSlider.getValue() * upper, jSlider.getMaximum());
    if (jToggleSymi.isSelected()) {
      SymLinkImage symLinkImage = SymLinkImages.deBoor(degree, upper + 1, parameter);
      graphics.drawImage(symLinkImage.bufferedImage(), 0, 0, null);
    }
    // ---
    GraphicsUtil.setQualityHigh(graphics);
    renderControlPoints(geometricLayer, graphics); // control points
    // ---
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor effective = control;
    if (jToggleItrp.isSelected()) {
      LieGroup lieGroup = geodesicDisplay.lieGroup();
      AbstractBSplineInterpolation abstractBSplineInterpolation = Objects.isNull(lieGroup) //
          ? new GeodesicBSplineInterpolation(geodesicDisplay.geodesicInterface(), degree, control)
          : new LieGroupBSplineInterpolation(lieGroup, geodesicDisplay.geodesicInterface(), degree, control);
      {
        Tensor tensor = BSplineInterpolationSequence.of(abstractBSplineInterpolation);
        Tensor shape = CirclePoints.of(9).multiply(RealScalar.of(0.05));
        graphics.setColor(new Color(64, 64, 64, 64));
        for (Tensor ctrls : tensor)
          for (Tensor ctrl : ctrls) {
            geometricLayer.pushMatrix(geodesicDisplay.matrixLift(ctrl));
            Path2D path2d = geometricLayer.toPath2D(shape);
            graphics.fill(path2d);
            geometricLayer.popMatrix();
          }
        graphics.setColor(new Color(64, 64, 64, 192));
        for (Tensor ctrls : Transpose.of(tensor))
          graphics.draw(geometricLayer.toPath2D(Tensor.of(ctrls.stream().map(geodesicDisplay::toPoint))));
      }
      Iteration iteration = abstractBSplineInterpolation.untilClose(Chop._06, 100);
      {
        graphics.setColor(Color.BLACK);
        graphics.drawString("" + iteration.steps(), 0, 20);
      }
      effective = iteration.control();
    }
    GeodesicBSplineFunction geodesicBSplineFunction = //
        GeodesicBSplineFunction.of(geodesicDisplay.geodesicInterface(), degree, effective);
    Tensor refined = Subdivide.of(0, upper, Math.max(1, upper * (1 << (levels)))).map(geodesicBSplineFunction);
    {
      Tensor selected = geodesicBSplineFunction.apply(parameter);
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(selected));
      Path2D path2d = geometricLayer.toPath2D(geodesicDisplay.shape());
      graphics.setColor(Color.DARK_GRAY);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
    renderCurve(render, false, geometricLayer, graphics);
    if (levels < 5)
      renderPoints(geometricLayer, graphics, refined);
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new BSplineFunctionDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
