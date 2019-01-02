// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.stream.IntStream;

import javax.swing.JSlider;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.CurveRender;
import ch.ethz.idsc.sophus.app.api.DubinsGenerator;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.curve.LagrangeInterpolation;
import ch.ethz.idsc.sophus.symlink.SymGeodesic;
import ch.ethz.idsc.sophus.symlink.SymLinkImage;
import ch.ethz.idsc.sophus.symlink.SymScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.N;

/** Bezier curve */
/* package */ class LagrangeInterpolationDemo extends ControlPointsDemo {
  private static final Tensor ARROWHEAD_LO = Arrowhead.of(0.18);
  // ---
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleComb = new JToggleButton("comb");
  private final JToggleButton jToggleSymi = new JToggleButton("graph");
  private final JSlider jSlider = new JSlider(0, 1000, 500);

  LagrangeInterpolationDemo() {
    super(true, GeodesicDisplays.ALL);
    // ---
    jToggleComb.setSelected(true);
    timerFrame.jToolBar.add(jToggleComb);
    // ---
    timerFrame.jToolBar.addSeparator();
    addButtonDubins();
    // ---
    jToggleSymi.setSelected(true);
    timerFrame.jToolBar.add(jToggleSymi);
    // ---
    spinnerRefine.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
    spinnerRefine.setValue(7);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    {
      Tensor blub = Tensors.fromString("{{1,0,0},{1,0,2.1}}");
      setControl(DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
          Tensor.of(blub.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))))));
    }
    // ---
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final Tensor control = control();
    final Scalar parameter = RationalScalar.of(jSlider.getValue(), jSlider.getMaximum());
    if (jToggleSymi.isSelected()) {
      Tensor vector = Tensor.of(IntStream.range(0, control.length()).mapToObj(SymScalar::leaf));
      ScalarTensorFunction scalarTensorFunction = LagrangeInterpolation.of(SymGeodesic.INSTANCE, vector)::at;
      Scalar scalar = N.DOUBLE.apply(parameter.multiply(RealScalar.of(control.length() - 1)));
      SymScalar symScalar = (SymScalar) scalarTensorFunction.apply(scalar);
      graphics.drawImage(new SymLinkImage(symScalar).bufferedImage(), 0, 0, null);
    }
    // ---
    GraphicsUtil.setQualityHigh(graphics);
    renderControlPoints(geometricLayer, graphics);
    // ---
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    int levels = spinnerRefine.getValue();
    Interpolation interpolation = LagrangeInterpolation.of(geodesicDisplay.geodesicInterface(), control());
    Tensor refined = Subdivide.of(0, control.length() - 1, 1 << levels).map(interpolation::at);
    new CurveRender(refined, false, jToggleComb.isSelected()).render(geometricLayer, graphics);
    {
      Tensor selected = interpolation.at(parameter.multiply(RealScalar.of(control.length() - 1)));
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(selected));
      Path2D path2d = geometricLayer.toPath2D(geodesicDisplay.shape());
      graphics.setColor(Color.DARK_GRAY);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    if (levels < 5)
      renderPoints(geometricLayer, graphics, refined);
  }

  public static void main(String[] args) {
    LagrangeInterpolationDemo bezierDemo = new LagrangeInterpolationDemo();
    bezierDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    bezierDemo.timerFrame.jFrame.setVisible(true);
  }
}
