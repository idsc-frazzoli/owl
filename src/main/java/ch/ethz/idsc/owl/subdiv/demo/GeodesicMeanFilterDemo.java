// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;

import javax.swing.JTextField;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.group.RnGeodesic;
import ch.ethz.idsc.owl.math.group.Se2CoveringGeodesic;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.owl.math.planar.CurvatureComb;
import ch.ethz.idsc.owl.subdiv.curve.BSpline4CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.BezierCurve;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicMeanFilter;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;

class GeodesicMeanFilterDemo extends ControlPointsDemo {
  private static final Tensor ARROWHEAD_LO = Arrowhead.of(0.18);
  private static final Scalar COMB_SCALE = DoubleScalar.of(1); // .5 (1 for presentation)
  private static final Color COLOR_CURVATURE_COMB = new Color(0, 0, 0, 128);
  // ---
  private final SpinnerLabel<Integer> spinnerRadius = new SpinnerLabel<>();
  private final JToggleButton jToggleCtrl = new JToggleButton("ctrl");
  private final JToggleButton jToggleBndy = new JToggleButton("bndy");
  private final JToggleButton jToggleComb = new JToggleButton("comb");
  private final JToggleButton jToggleLine = new JToggleButton("line");

  GeodesicMeanFilterDemo() {
    {
      Tensor blub = Tensors.fromString("{{1,0,0},{1,0,0},{2,0,2.5708},{1,0,2.1},{1.5,0,0},{2.3,0,-1.2},{1.5,0,0},{4,0,3.14159},{2,0,3.14159},{2,0,0}}");
      setControl(DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
          Tensor.of(blub.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))))));
    }
    {
      // JButton jButton = new JButton("clear");
      // jButton.addActionListener(actionEvent -> control = Tensors.of(Array.zeros(3)));
      // timerFrame.jToolBar.add(jButton);
    }
    JTextField jTextField = new JTextField(10);
    jTextField.setPreferredSize(new Dimension(100, 28));
    {
      timerFrame.jToolBar.add(jTextField);
    }
    {
      // JButton jButton = new JButton("print");
      // jButton.addActionListener(actionEvent -> {
      // System.out.println(control);
      // // long now = System.currentTimeMillis();
      // File file = UserHome.file("" + jTextField.getText() + ".csv");
      // // File file = new File("src/main/resources/subdiv/se2", now + ".csv");
      // try {
      // Export.of(file, control.map(CsvFormat.strict()));
      // } catch (Exception exception) {
      // exception.printStackTrace();
      // }
      // });
      // timerFrame.jToolBar.add(jButton);
    }
    jToggleCtrl.setSelected(true);
    timerFrame.jToolBar.add(jToggleCtrl);
    // ---
    jToggleBndy.setSelected(true);
    timerFrame.jToolBar.add(jToggleBndy);
    // ---
    jToggleComb.setSelected(true);
    timerFrame.jToolBar.add(jToggleComb);
    // ---
    jToggleLine.setSelected(false);
    timerFrame.jToolBar.add(jToggleLine);
    // ---
    // jToggleButton.setSelected(Dimensions.of(control).get(1) == 2);
    timerFrame.jToolBar.add(jToggleButton);
    // ---
    spinnerRadius.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    spinnerRadius.setValue(9);
    spinnerRadius.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // graphics.drawImage(image, 100, 100, null);
    GraphicsUtil.setQualityHigh(graphics);
    boolean isR2 = jToggleButton.isSelected();
    Tensor _control = controlSe2();
    int radius = spinnerRadius.getValue();
    final Tensor refined;
    final Tensor curve;
    if (isR2) {
      Tensor rnctrl = controlR2();
      // refined = LanczosCurve.refine(rnctrl, 1 << levels);
      graphics.setColor(new Color(255, 128, 128, 255));
      for (Tensor point : _control) {
        geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point.copy().append(RealScalar.ZERO)));
        Path2D path2d = geometricLayer.toPath2D(CIRCLE_HI);
        path2d.closePath();
        graphics.setColor(new Color(255, 128, 128, 64));
        graphics.fill(path2d);
        graphics.setColor(new Color(255, 128, 128, 255));
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
      TensorUnaryOperator geodesicMeanFilter = GeodesicMeanFilter.of(RnGeodesic.INSTANCE, radius);
      refined = geodesicMeanFilter.apply(rnctrl);
      curve = Nest.of(BSpline4CurveSubdivision.of(RnGeodesic.INSTANCE)::string, refined, 7);
    } else { // SE2
      if (jToggleCtrl.isSelected())
        for (Tensor point : controlSe2()) {
          geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point));
          Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_HI);
          path2d.closePath();
          graphics.setColor(new Color(255, 128, 128, 64));
          graphics.fill(path2d);
          graphics.setColor(new Color(255, 128, 128, 255));
          graphics.draw(path2d);
          geometricLayer.popMatrix();
        }
      TensorUnaryOperator geodesicMeanFilter = GeodesicMeanFilter.of(Se2CoveringGeodesic.INSTANCE, radius);
      refined = geodesicMeanFilter.apply(controlSe2());
      curve = Nest.of(BSpline4CurveSubdivision.of(Se2CoveringGeodesic.INSTANCE)::string, refined, 7);
    }
    if (jToggleLine.isSelected()) {
      BezierCurve bezierCurve = new BezierCurve(Se2CoveringGeodesic.INSTANCE);
      Tensor linear = bezierCurve.refine(_control, 1 << 8);
      graphics.setColor(new Color(0, 255, 0, 128));
      Path2D path2d = geometricLayer.toPath2D(linear);
      graphics.draw(path2d);
    }
    {
      graphics.setColor(Color.BLUE);
      Path2D path2d = geometricLayer.toPath2D(curve);
      graphics.setStroke(new BasicStroke(1.25f));
      graphics.draw(path2d);
      graphics.setStroke(new BasicStroke(1f));
    }
    if (jToggleComb.isSelected()) {
      graphics.setColor(COLOR_CURVATURE_COMB);
      Path2D path2d = geometricLayer.toPath2D(CurvatureComb.of(refined, COMB_SCALE, false));
      graphics.draw(path2d);
    }
    for (Tensor point : refined) {
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point.copy().append(RealScalar.ZERO)));
      Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_LO);
      geometricLayer.popMatrix();
      int rgb = 128 + 32;
      path2d.closePath();
      graphics.setColor(new Color(rgb, rgb, rgb, 128 + 64));
      graphics.fill(path2d);
      graphics.setColor(Color.BLACK);
      graphics.draw(path2d);
    }
  }

  public static void main(String[] args) {
    GeodesicMeanFilterDemo geodesicMeanFilterDemo = new GeodesicMeanFilterDemo();
    geodesicMeanFilterDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    geodesicMeanFilterDemo.timerFrame.jFrame.setVisible(true);
  }
}
