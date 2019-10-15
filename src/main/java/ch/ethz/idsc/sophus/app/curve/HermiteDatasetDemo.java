// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import javax.swing.JToggleButton;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDatasetDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import ch.ethz.idsc.sophus.app.io.GokartPoseDatas;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.crv.subdiv.HermiteSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.HermiteSubdivisions;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.sca.Power;

/* package */ class HermiteDatasetDemo extends GeodesicDatasetDemo {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 360;
  private static final Color COLOR_CURVE = new Color(255, 128, 128, 255);
  private static final Color COLOR_SHAPE = new Color(160, 160, 160, 160);
  private static final Color COLOR_RECON = new Color(128, 128, 128, 255);
  // ---
  private final PathRender pathRenderCurve = new PathRender(COLOR_CURVE);
  private final PathRender pathRenderShape = new PathRender(COLOR_RECON, 2f);
  // ---
  private final GokartPoseDataV2 gokartPoseData;
  private final SpinnerLabel<Integer> spinnerLabelSkips = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerLabelShift = new SpinnerLabel<>();
  private final SpinnerLabel<HermiteSubdivisions> spinnerLabelScheme = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerLabelLevel = new SpinnerLabel<>();
  private final JToggleButton jToggleSpace = new JToggleButton("d-space");
  private final JToggleButton jToggleButton = new JToggleButton("derivatives");
  protected Tensor _control = Tensors.empty();

  public HermiteDatasetDemo(GokartPoseDataV2 gokartPoseData) {
    super(GeodesicDisplays.SE2_ONLY, gokartPoseData);
    this.gokartPoseData = gokartPoseData;
    timerFrame.geometricComponent.setModel2Pixel(GokartPoseDatas.HANGAR_MODEL2PIXEL);
    {
      spinnerLabelSkips.setList(Arrays.asList(1, 2, 5, 10, 25, 50, 100));
      spinnerLabelSkips.setValue(50);
      spinnerLabelSkips.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "skips");
      spinnerLabelSkips.addSpinnerListener(type -> updateState());
    }
    {
      spinnerLabelShift.setList(Arrays.asList(0, 2, 4, 6, 8, 10, 15, 20));
      spinnerLabelShift.setValue(0);
      spinnerLabelShift.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "shift");
      spinnerLabelShift.addSpinnerListener(type -> updateState());
    }
    timerFrame.jToolBar.addSeparator();
    {
      spinnerLabelScheme.setArray(HermiteSubdivisions.values());
      spinnerLabelScheme.setValue(HermiteSubdivisions.HERMITE1);
      spinnerLabelScheme.addToComponentReduced(timerFrame.jToolBar, new Dimension(140, 28), "scheme");
    }
    {
      spinnerLabelLevel.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
      spinnerLabelLevel.setValue(3);
      spinnerLabelLevel.addToComponentReduced(timerFrame.jToolBar, new Dimension(40, 28), "level");
      // spinnerLabelLevel.addSpinnerListener(type -> updateState());
    }
    timerFrame.jToolBar.addSeparator();
    {
      jToggleSpace.setSelected(true);
      jToggleSpace.setToolTipText("show derivatives");
      timerFrame.jToolBar.add(jToggleSpace);
    }
    {
      jToggleButton.setSelected(true);
      jToggleButton.setToolTipText("show derivatives");
      timerFrame.jToolBar.add(jToggleButton);
    }
    updateState();
  }

  @Override
  protected void updateState() {
    int limit = spinnerLabelLimit.getValue();
    String name = spinnerLabelString.getValue();
    Tensor control = gokartPoseData.getPoseVel(name, limit);
    Tensor result = Tensors.empty();
    int skips = spinnerLabelSkips.getValue();
    int offset = spinnerLabelShift.getValue();
    for (int index = offset; index < control.length(); index += skips)
      result.append(control.get(index));
    // TensorUnaryOperator centerFilter = //
    // CenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, GaussianWindow.FUNCTION), 4);
    _control = result;
  }

  @SuppressWarnings("unused")
  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    {
      final Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(0.3));
      pathRenderCurve.setCurve(_control.get(Tensor.ALL, 0), false).render(geometricLayer, graphics);
      if (_control.length() <= 1000)
        for (Tensor point : _control.get(Tensor.ALL, 0)) {
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
    graphics.setColor(Color.DARK_GRAY);
    Scalar delta = RationalScalar.of(spinnerLabelSkips.getValue(), 50);
    HermiteSubdivision hermiteSubdivision = //
        spinnerLabelScheme.getValue().supply(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, Se2BiinvariantMean.LINEAR);
    TensorIteration tensorIteration = hermiteSubdivision.string(delta, _control);
    Tensor refined = _control;
    int levels = spinnerLabelLevel.getValue();
    for (int level = 0; level < levels; ++level)
      refined = tensorIteration.iterate();
    pathRenderShape.setCurve(refined.get(Tensor.ALL, 0), false).render(geometricLayer, graphics);
    {
      new Se2HermitePlot(refined, RealScalar.of(0.3)).render(geometricLayer, graphics);
    }
    if (jToggleButton.isSelected()) {
      Tensor deltas = refined.get(Tensor.ALL, 1);
      int dims = deltas.get(0).length();
      if (0 < deltas.length()) {
        JFreeChart jFreeChart = StaticHelper.listPlot(deltas, //
            Range.of(0, deltas.length()).multiply(delta).divide(Power.of(2, levels)));
        Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
        jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
      }
    }
    if (false) {
      final Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(0.8));
      for (Tensor point : refined.get(Tensor.ALL, 0)) {
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
  }

  public static void main(String[] args) {
    new HermiteDatasetDemo(GokartPoseDataV2.RACING_DAY).setVisible(1000, 800);
  }
}
