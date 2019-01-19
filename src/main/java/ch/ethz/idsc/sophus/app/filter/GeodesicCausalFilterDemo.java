// code by jph
package ch.ethz.idsc.sophus.app.filter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.filter.GeodesicIIR3Filter;
import ch.ethz.idsc.sophus.group.LieDifferences;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.group.Se2Group;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.sophus.planar.Arrowhead;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

class GeodesicCausalFilterDemo extends AbstractDemo {
  private static final Tensor ARROWHEAD_HI = Arrowhead.of(0.10);
  private static final Tensor CIRCLE = CirclePoints.of(20).multiply(RealScalar.of(0.01));
  // ---
  // private final SpinnerLabel<SmoothingKernel> spinnerFilter = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRadius = new SpinnerLabel<>();
  private final JToggleButton jToggleCtrl = new JToggleButton("ctrl");
  private final JToggleButton jToggleLine = new JToggleButton("line");
  private final JToggleButton jToggleWait = new JToggleButton("wait");
  private final JToggleButton jToggleDiff = new JToggleButton("diff");
  private final JToggleButton jToggleStep = new JToggleButton("step");
  private Tensor control = Tensors.of(Array.zeros(3));
  private Scalar alpha = RationalScalar.HALF;
  private Scalar beta = RealScalar.of(2 / 3);

  GeodesicCausalFilterDemo() {
    {
      SpinnerLabel<String> spinnerLabel = new SpinnerLabel<>();
      List<String> list = ResourceData.lines("/dubilab/app/pose/index.txt");
      spinnerLabel.addSpinnerListener(resource -> //
      control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + resource + ".csv").stream() //
          .limit(200) //
          .map(row -> row.extract(1, 4))));
      spinnerLabel.setList(list);
      spinnerLabel.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "data");
    }
    JTextField jTextField = new JTextField(10);
    jTextField.setPreferredSize(new Dimension(100, 28));
    timerFrame.jToolBar.add(jTextField);
    // ---
    jToggleCtrl.setSelected(true);
    timerFrame.jToolBar.add(jToggleCtrl);
    // ---
    jToggleLine.setSelected(false);
    timerFrame.jToolBar.add(jToggleLine);
    // ---
    jToggleStep.setSelected(false);
    timerFrame.jToolBar.add(jToggleStep);
    // ---
    spinnerRadius.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    spinnerRadius.setValue(9);
    spinnerRadius.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // {
    // // spinnerFilter.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
    // spinnerFilter.setList(Arrays.asList(SmoothingKernel.values()));
    // spinnerFilter.setValue(SmoothingKernel.GAUSSIAN);
    // spinnerFilter.addToComponentReduced(timerFrame.jToolBar, new Dimension(180, 28), "filter");
    // }
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ColorDataIndexed cyclic = ColorDataLists._097.cyclic().deriveWithAlpha(192);
    if (jToggleStep.isSelected()) {
      control = Tensors.of(Array.zeros(3));
      for (int i = 0; i < 300; ++i) {
        if (i < 100) {
          control.append(Tensors.vector(i * 0.01, 0, 0));
        } else if (i > 200) {
          control.append(Tensors.vector(i * 0.01, 0, 0));
        } else {
          control.append(Tensors.vector(i * 0.01, 1, 0));
        }
      }
    }
    if (jToggleWait.isSelected())
      return;
    GraphicsUtil.setQualityHigh(graphics);
    if (jToggleCtrl.isSelected()) {
      if (jToggleLine.isSelected()) {
        graphics.setColor(cyclic.getColor(0));
        graphics.draw(geometricLayer.toPath2D(control));
      }
      for (Tensor xya : control) {
        geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
        Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_HI);
        if (jToggleStep.isSelected()) {
          path2d = geometricLayer.toPath2D(CIRCLE);
        }
        path2d.closePath();
        graphics.setColor(cyclic.getColor(0));
        graphics.fill(path2d);
        graphics.setColor(Color.BLACK);
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
    // TensorUnaryOperator geodesicCenterFilter = new GeodesicIIR2Filter(Se2Geodesic.INSTANCE, alpha);
    // TensorUnaryOperator geodesicCenterFilter = new GeodesicFIR3Filter(Se2Geodesic.INSTANCE, alpha, beta);
    TensorUnaryOperator geodesicCenterFilter = new GeodesicIIR3Filter(Se2Geodesic.INSTANCE, alpha, beta);
    final Tensor refined = Tensor.of(control.stream().map(geodesicCenterFilter));
    if (jToggleDiff.isSelected()) {
      final int baseline_y = 200;
      {
        graphics.setColor(Color.BLACK);
        graphics.drawLine(0, baseline_y, 300, baseline_y);
      }
      ColorDataIndexed colorDataIndexed = ColorDataLists._097.cyclic();
      {
        int piy = 30;
        // graphics.drawString("Window: " + Round._3.apply(width), 0, piy += 15);
        graphics.setColor(colorDataIndexed.getColor(0));
        graphics.drawString("Tangent velocity", 0, piy += 15);
        graphics.setColor(colorDataIndexed.getColor(1));
        graphics.drawString("Side slip", 0, piy += 15);
        graphics.setColor(colorDataIndexed.getColor(2));
        graphics.drawString("Rotational rate", 0, piy += 15);
      }
      LieDifferences lieDifferences = //
          new LieDifferences(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
      Tensor speeds = lieDifferences.apply(refined);
      for (int c = 0; c < 3; ++c) {
        graphics.setColor(colorDataIndexed.getColor(c));
        Path2D path2d = plotFunc(graphics, speeds.get(Tensor.ALL, c).multiply(RealScalar.of(400)), baseline_y);
        graphics.setStroke(new BasicStroke(1.3f));
        graphics.draw(path2d);
      }
    }
    graphics.setStroke(new BasicStroke(1f));
    if (jToggleLine.isSelected()) {
      graphics.setColor(cyclic.getColor(1));
      graphics.draw(geometricLayer.toPath2D(refined));
    }
    for (Tensor point : refined) {
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point.copy().append(RealScalar.ZERO)));
      Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_HI);
      if (jToggleStep.isSelected()) {
        path2d = geometricLayer.toPath2D(CIRCLE);
      }
      geometricLayer.popMatrix();
      path2d.closePath();
      graphics.setColor(cyclic.getColor(1));
      graphics.fill(path2d);
      graphics.setColor(Color.BLACK);
      graphics.draw(path2d);
    }
    {
      JSlider jSlider = new JSlider(1, 999, 500);
      jSlider.setPreferredSize(new Dimension(500, 28));
      jSlider.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent changeEvent) {
          alpha = RationalScalar.of(jSlider.getValue(), 1000);
          System.out.println(alpha);
        }
      });
      timerFrame.jToolBar.add(jSlider);
    }
    // Only for higher order 3 and higher relevant
    {
      JSlider jSlider2 = new JSlider(1, 999, 500);
      jSlider2.setPreferredSize(new Dimension(500, 28));
      jSlider2.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent changeEvent) {
          beta = RationalScalar.of(jSlider2.getValue(), 1000);
          System.out.println(beta);
        }
      });
      timerFrame.jToolBar.add(jSlider2);
    }
    {
      spinnerRadius.setList(IntStream.range(0, 21).boxed().collect(Collectors.toList()));
      spinnerRadius.setValue(6);
      spinnerRadius.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    }
  }

  protected Path2D plotFunc(Graphics2D graphics, Tensor tensor, int baseline_y) {
    Path2D path2d = new Path2D.Double();
    if (Tensors.nonEmpty(tensor))
      path2d.moveTo(0, baseline_y - tensor.Get(0).number().doubleValue());
    for (int pix = 1; pix < tensor.length(); ++pix)
      path2d.lineTo(pix, baseline_y - tensor.Get(pix).number().doubleValue());
    return path2d;
  }

  public static void main(String[] args) {
    GeodesicCausalFilterDemo geodesicCenterFilterDemo = new GeodesicCausalFilterDemo();
    geodesicCenterFilterDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 800);
    geodesicCenterFilterDemo.timerFrame.jFrame.setVisible(true);
    geodesicCenterFilterDemo.timerFrame.geometricComponent.setModel2Pixel( //
        Tensors.fromString("{{7.5,0,100},{0,-7.5,800},{0,0,1}}"));
  }
}
