// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.CurveRender;
import ch.ethz.idsc.sophus.app.api.DubinsGenerator;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.curve.GeodesicBSplineFunction;
import ch.ethz.idsc.sophus.curve.LieGroupBSplineInterpolation;
import ch.ethz.idsc.sophus.symlink.SymLinkImage;
import ch.ethz.idsc.sophus.symlink.SymLinkImages;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;

/* package */ class BSplineFunctionDemo extends ControlPointsDemo {
  private static final List<Integer> DEGREES = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
  // ---
  private final SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleCtrl = new JToggleButton("ctrl");
  private final JToggleButton jToggleComb = new JToggleButton("comb");
  private final JToggleButton jToggleItrp = new JToggleButton("interp");
  private final JToggleButton jToggleLine = new JToggleButton("line");
  private final JToggleButton jToggleSymi = new JToggleButton("graph");
  private final JSlider jSlider = new JSlider(0, 1000, 500);

  BSplineFunctionDemo() {
    super(false, GeodesicDisplays.ALL);
    {
      JButton jButton = new JButton("clear");
      jButton.addActionListener(actionEvent -> setControl(Tensors.of(Array.zeros(3), Tensors.vector(1, 0, 0))));
      timerFrame.jToolBar.add(jButton);
    }
    jToggleCtrl.setSelected(true);
    timerFrame.jToolBar.add(jToggleCtrl);
    // ---
    jToggleComb.setSelected(true);
    timerFrame.jToolBar.add(jToggleComb);
    // ---
    jToggleLine.setSelected(false);
    timerFrame.jToolBar.add(jToggleLine);
    // ---
    timerFrame.jToolBar.addSeparator();
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
    Tensor effective = jToggleItrp.isSelected() //
        ? new LieGroupBSplineInterpolation(geodesicDisplay.lieGroup(), geodesicDisplay.geodesicInterface(), degree, control).apply()
        : control;
    GeodesicBSplineFunction geodesicBSplineFunction = //
        GeodesicBSplineFunction.of(geodesicDisplay.geodesicInterface(), degree, effective);
    Tensor refined = Subdivide.of(0, upper, upper * (1 << (levels))).map(geodesicBSplineFunction);
    {
      Tensor selected = geodesicBSplineFunction.apply(parameter);
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(selected));
      Path2D path2d = geometricLayer.toPath2D(geodesicDisplay.shape());
      graphics.setColor(Color.DARK_GRAY);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    new CurveRender(refined, false, jToggleComb.isSelected()).render(geometricLayer, graphics); // limit curve
    if (levels < 5)
      renderPoints(geometricLayer, graphics, refined);
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new BSplineFunctionDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
