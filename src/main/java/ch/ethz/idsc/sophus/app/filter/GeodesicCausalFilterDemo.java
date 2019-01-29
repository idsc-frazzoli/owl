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
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplayDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.filter.GeodesicFIRnFilter;
import ch.ethz.idsc.sophus.filter.GeodesicIIRnFilter;
import ch.ethz.idsc.sophus.group.LieDifferences;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Group;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.sophus.math.SmoothingKernelCausal;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

class GeodesicCausalFilterDemo extends GeodesicDisplayDemo {
  private static final Color COLOR_CURVE = new Color(255, 128, 128, 255);
  private static final Color COLOR_SHAPE = new Color(160, 160, 160, 192);
  private final SpinnerLabel<SmoothingKernelCausal> spinnerFilter = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRadius = new SpinnerLabel<>();
  private final JToggleButton jToggleCtrl = new JToggleButton("ctrl");
  private final JToggleButton jToggleLine = new JToggleButton("line");
  private final JToggleButton jToggleWait = new JToggleButton("wait");
  private final JToggleButton jToggleDiff = new JToggleButton("diff");
  private final JToggleButton jToggleStep = new JToggleButton("step");
  private final JToggleButton jToggleIIR = new JToggleButton("IIR");
  private final PathRender pathRenderCurve = new PathRender(COLOR_CURVE);
  private Tensor _control = Tensors.of(Array.zeros(3));
  private Scalar alpha = RationalScalar.of(3, 4);

  GeodesicCausalFilterDemo() {
    super(GeodesicDisplays.SE2_R2);
    {
      SpinnerLabel<String> spinnerLabel = new SpinnerLabel<>();
      List<String> list = ResourceData.lines("/dubilab/app/pose/index.txt");
      spinnerLabel.addSpinnerListener(resource -> //
      _control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + resource + ".csv").stream() //
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
    jToggleIIR.setSelected(false);
    timerFrame.jToolBar.add(jToggleIIR);
    // ---
    {
      spinnerFilter.setList(Arrays.asList(SmoothingKernelCausal.values()));
      spinnerFilter.setValue(SmoothingKernelCausal.GAUSSIAN);
      spinnerFilter.addToComponentReduced(timerFrame.jToolBar, new Dimension(180, 28), "filter");
    }
    {
      spinnerRadius.setList(IntStream.range(0, 40).boxed().collect(Collectors.toList()));
      spinnerRadius.setValue(9);
      spinnerRadius.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    }
  }

  public final Tensor control() {
    return Tensor.of(_control.stream().map(geodesicDisplay()::project)).unmodifiable();
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor control = control();
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    final SmoothingKernelCausal smoothingKernelCausal = spinnerFilter.getValue();
    final Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(.3));
    if (jToggleStep.isSelected()) {
      control = Tensors.of(Array.zeros(3));
      for (int i = 0; i < 300; ++i) {
        if (i < 100) {
          control.append(Tensors.vector(i * 0.01, 0, 0));
        } else //
        if (i > 200) {
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
      if (jToggleLine.isSelected())
        pathRenderCurve.setCurve(control, false).render(geometricLayer, graphics);
      for (Tensor point : control) {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(shape);
        path2d.closePath();
        graphics.setColor(new Color(255, 128, 128, 64));
        graphics.fill(path2d);
        graphics.setColor(COLOR_SHAPE);
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
    Tensor mask = smoothingKernelCausal.apply(spinnerRadius.getValue());
    mask.append(alpha);
    TensorUnaryOperator geodesicCenterFilter;
    if (jToggleIIR.isSelected()) {
      geodesicCenterFilter = new GeodesicIIRnFilter(geodesicDisplay.geodesicInterface(), mask);
    } else {
      geodesicCenterFilter = new GeodesicFIRnFilter(geodesicDisplay.geodesicInterface(), mask);
    }
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
      graphics.draw(geometricLayer.toPath2D(refined));
      pathRenderCurve.setCurve(control, false).render(geometricLayer, graphics);
    }
    for (Tensor point : refined) {
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point.copy().append(RealScalar.ZERO)));
      Path2D path2d = geometricLayer.toPath2D(shape);
      if (jToggleStep.isSelected()) {
        path2d = geometricLayer.toPath2D(shape);
      }
      geometricLayer.popMatrix();
      path2d.closePath();
      graphics.setColor(COLOR_SHAPE);
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
