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

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.app.util.SpinnerListener;
import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.group.LieDifferences;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.math.WindowCenterSampler;
import ch.ethz.idsc.sophus.sym.SymLinkImages;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class GeodesicCenterFilterDemo extends DatasetFilterDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();
  private static final Color COLOR_CURVE = new Color(255, 128, 128, 255);
  private static final Color COLOR_SHAPE = new Color(160, 160, 160, 192);
  // ---
  private final SpinnerLabel<SmoothingKernel> spinnerFilter = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRadius = new SpinnerLabel<>();
  private final JToggleButton jToggleData = new JToggleButton("data");
  private final JToggleButton jToggleLine = new JToggleButton("line");
  private final JToggleButton jToggleDiff = new JToggleButton("diff");
  private final JToggleButton jToggleSymi = new JToggleButton("graph");
  private final JToggleButton jToggleWait = new JToggleButton("wait");
  private final PathRender pathRenderCurve = new PathRender(COLOR_CURVE);
  private final PathRender pathRenderShape = new PathRender(COLOR_SHAPE);
  // ---
  // /* package */ final SpinnerLabel<GeodesicDisplay> geodesicDisplaySpinner = new SpinnerLabel<>();
  private Tensor _control = Tensors.of(Array.zeros(3));
  private final SpinnerListener<String> spinnerListener = resource -> //
  _control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + resource + ".csv").stream() //
      .limit(300) //
      .map(row -> row.extract(1, 4)));

  GeodesicCenterFilterDemo() {
    timerFrame.geometricComponent.setModel2Pixel(StaticHelper.HANGAR_MODEL2PIXEL);
    {
      SpinnerLabel<String> spinnerLabel = new SpinnerLabel<>();
      List<String> list = ResourceData.lines("/dubilab/app/pose/index.txt");
      spinnerLabel.addSpinnerListener(spinnerListener);
      spinnerLabel.setList(list);
      spinnerLabel.setIndex(0);
      spinnerLabel.reportToAll();
      spinnerLabel.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "data");
    }
    // {
    // List<String> list = ResourceData.lines("/dubilab/app/pose/index.txt");
    // JComboBox<String> jComboBox = new JComboBox<>(list.toArray(new String[list.size()]));
    // jComboBox.setLightWeightPopupEnabled(true);
    // jComboBox.setMaximumRowCount(30);
    // package ch.ethz.idsc.sophus.filter; jComboBox.addActionListener(actionEvent -> spinnerListener.actionPerformed(list.get(jComboBox.getSelectedIndex())));
    // timerFrame.jToolBar.add(jComboBox);
    // }
    // ---
    jToggleData.setSelected(true);
    timerFrame.jToolBar.add(jToggleData);
    // ---
    jToggleLine.setSelected(true);
    timerFrame.jToolBar.add(jToggleLine);
    // ---
    jToggleDiff.setSelected(true);
    timerFrame.jToolBar.add(jToggleDiff);
    // ---
    timerFrame.jToolBar.add(jToggleSymi);
    // ---
    jToggleWait.setSelected(false);
    timerFrame.jToolBar.add(jToggleWait);
    // ---
    {
      spinnerFilter.setList(Arrays.asList(SmoothingKernel.values()));
      spinnerFilter.setValue(SmoothingKernel.GAUSSIAN);
      spinnerFilter.addToComponentReduced(timerFrame.jToolBar, new Dimension(180, 28), "filter");
    }
    {
      spinnerRadius.setList(IntStream.range(0, 21).boxed().collect(Collectors.toList()));
      spinnerRadius.setValue(6);
      spinnerRadius.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    }
  }

  public final Tensor control() {
    return Tensor.of(_control.stream().map(geodesicDisplay()::project)).unmodifiable();
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final SmoothingKernel smoothingKernel = spinnerFilter.getValue();
    final GeodesicDisplay geodesicDisplay = geodesicDisplay();
    final Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(.3));
    GraphicsUtil.setQualityHigh(graphics);
    final int radius = spinnerRadius.getValue();
    if (jToggleSymi.isSelected())
      graphics.drawImage(SymLinkImages.smoothingKernel(smoothingKernel, radius).bufferedImage(), 0, 0, null);
    // ---
    if (jToggleWait.isSelected())
      return;
    // ---
    Tensor control2 = control();
    if (jToggleData.isSelected()) {
      if (jToggleLine.isSelected())
        pathRenderCurve.setCurve(control2, false).render(geometricLayer, graphics);
      for (Tensor point : control2) {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(shape);
        path2d.closePath();
        graphics.setColor(new Color(255, 128, 128, 64));
        graphics.fill(path2d);
        graphics.setColor(COLOR_CURVE);
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
    WindowCenterSampler centerWindowSampler = new WindowCenterSampler(smoothingKernel);
    TensorUnaryOperator geodesicCenterFilter = //
        GeodesicCenterFilter.of(GeodesicCenter.of(geodesicDisplay.geodesicInterface(), centerWindowSampler), radius);
    final Tensor refined = geodesicCenterFilter.apply(control2);
    if (jToggleDiff.isSelected()) {
      final int baseline_y = 200;
      {
        graphics.setColor(Color.BLACK);
        graphics.drawLine(0, baseline_y, 300, baseline_y);
      }
      {
        int piy = 30;
        graphics.drawString("Filter: " + spinnerFilter.getValue(), 0, piy);
        Scalar width = Quantity.of(0.05 * (spinnerRadius.getValue() * 2 + 1), "s");
        graphics.drawString("Window: " + Round._3.apply(width), 0, piy += 15);
        graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
        graphics.drawString("Tangent velocity", 0, piy += 15);
        graphics.setColor(COLOR_DATA_INDEXED.getColor(1));
        graphics.drawString("Side slip", 0, piy += 15);
        graphics.setColor(COLOR_DATA_INDEXED.getColor(2));
        graphics.drawString("Rotational rate", 0, piy += 15);
      }
      {
        LieDifferences lieDifferences = //
            new LieDifferences(geodesicDisplay.lieGroup(), geodesicDisplay.lieExponential());
        Tensor speeds = lieDifferences.apply(refined);
        if (0 < speeds.length()) {
          int dimensions = speeds.get(0).length();
          graphics.setStroke(new BasicStroke(1.3f));
          for (int index = 0; index < dimensions; ++index) {
            graphics.setColor(COLOR_DATA_INDEXED.getColor(index));
            Path2D path2d = plotFunc(graphics, speeds.get(Tensor.ALL, index).multiply(RealScalar.of(400)), baseline_y);
            graphics.draw(path2d);
          }
        }
      }
    }
    graphics.setStroke(new BasicStroke(1f));
    if (jToggleLine.isSelected())
      pathRenderShape.setCurve(refined, false).render(geometricLayer, graphics);
    for (Tensor point : refined) {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
      Path2D path2d = geometricLayer.toPath2D(shape);
      path2d.closePath();
      graphics.setColor(COLOR_SHAPE);
      graphics.fill(path2d);
      graphics.setColor(Color.BLACK);
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
  }

  protected Path2D plotFunc(Graphics2D graphics, Tensor tensor, int baseline_y) {
    Path2D path2d = new Path2D.Double();
    if (Tensors.nonEmpty(tensor))
      path2d.moveTo(0, baseline_y - tensor.Get(0).number().doubleValue());
    for (int pix = 1; pix < tensor.length(); ++pix)
      path2d.lineTo(pix * 0.5, baseline_y - tensor.Get(pix).number().doubleValue());
    return path2d;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new GeodesicCenterFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
