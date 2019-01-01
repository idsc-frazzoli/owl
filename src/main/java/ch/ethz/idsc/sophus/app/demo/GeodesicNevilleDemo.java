// code by jph
package ch.ethz.idsc.sophus.app.demo;

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
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.sophus.curve.LagrangeInterpolation;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2CoveringGeodesic;
import ch.ethz.idsc.sophus.symlink.SymGeodesic;
import ch.ethz.idsc.sophus.symlink.SymLinkImage;
import ch.ethz.idsc.sophus.symlink.SymScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.N;

/** Bezier curve */
/* package */ class GeodesicNevilleDemo extends ControlPointsDemo {
  private static final Tensor ARROWHEAD_LO = Arrowhead.of(0.18);
  // ---
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleComb = new JToggleButton("comb");
  private final JToggleButton jToggleSymi = new JToggleButton("graph");
  // ---
  private Scalar parameter = RationalScalar.HALF;

  GeodesicNevilleDemo() {
    timerFrame.jToolBar.add(jButton);
    // ---
    jToggleComb.setSelected(true);
    timerFrame.jToolBar.add(jToggleComb);
    // ---
    timerFrame.jToolBar.addSeparator();
    addButtonDubins();
    // ---
    timerFrame.jToolBar.add(jToggleButton);
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
    JSlider jSlider = new JSlider(0, 1000, 500);
    jSlider.setPreferredSize(new Dimension(500, 28));
    jSlider.addChangeListener(changeEvent -> parameter = RationalScalar.of(jSlider.getValue(), 1000));
    timerFrame.jToolBar.add(jSlider);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor _control = controlSe2();
    if (jToggleSymi.isSelected()) {
      Tensor vector = Tensor.of(IntStream.range(0, _control.length()).mapToObj(SymScalar::leaf));
      ScalarTensorFunction scalarTensorFunction = LagrangeInterpolation.of(SymGeodesic.INSTANCE, vector)::at;
      Scalar scalar = N.DOUBLE.apply(parameter.multiply(RealScalar.of(_control.length() - 1)));
      SymScalar symScalar = (SymScalar) scalarTensorFunction.apply(scalar);
      graphics.drawImage(new SymLinkImage(symScalar).bufferedImage(), 0, 0, null);
    }
    // ---
    GraphicsUtil.setQualityHigh(graphics);
    boolean isR2 = jToggleButton.isSelected();
    int levels = spinnerRefine.getValue();
    renderControlPoints(geometricLayer, graphics);
    Tensor domain = Subdivide.of(0, controlR2().length() - 1, 1 << levels);
    Tensor refined = isR2 //
        ? domain.map(LagrangeInterpolation.of(RnGeodesic.INSTANCE, controlR2())::at)
        : domain.map(LagrangeInterpolation.of(Se2CoveringGeodesic.INSTANCE, _control)::at);
    new CurveRender(refined, false, jToggleComb.isSelected()).render(geometricLayer, graphics);
    if (!isR2 && levels < 5)
      for (Tensor point : refined) {
        geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point));
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
    GeodesicNevilleDemo bezierDemo = new GeodesicNevilleDemo();
    bezierDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    bezierDemo.timerFrame.jFrame.setVisible(true);
  }
}
