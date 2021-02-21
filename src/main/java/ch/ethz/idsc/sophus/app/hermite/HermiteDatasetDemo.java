// code by jph
package ch.ethz.idsc.sophus.app.hermite;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import ch.ethz.idsc.sophus.app.io.GokartPoseDatas;
import ch.ethz.idsc.sophus.gds.GeodesicDatasetDemo;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.lie.so2.So2Lift;
import ch.ethz.idsc.sophus.math.Do;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.sophus.opt.HermiteSubdivisions;
import ch.ethz.idsc.sophus.ref.d1h.HermiteSubdivision;
import ch.ethz.idsc.tensor.NumberQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.sca.Power;

/* package */ class HermiteDatasetDemo extends GeodesicDatasetDemo {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 360;
  private static final Color COLOR_CURVE = new Color(255, 128, 128, 255);
  private static final Color COLOR_RECON = new Color(128, 128, 128, 255);
  // ---
  private static final Stroke STROKE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  private final PathRender pathRenderCurve = new PathRender(COLOR_CURVE, STROKE);
  private final PathRender pathRenderShape = new PathRender(COLOR_RECON, 2f);
  // ---
  private final GokartPoseDataV2 gokartPoseData;
  private final SpinnerLabel<Integer> spinnerLabelSkips = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerLabelShift = new SpinnerLabel<>();
  private final SpinnerLabel<HermiteSubdivisions> spinnerLabelScheme = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerLabelLevel = new SpinnerLabel<>();
  private final JToggleButton jToggleButton = new JToggleButton("diff");
  protected Tensor _control = Tensors.empty();

  public HermiteDatasetDemo(GokartPoseDataV2 gokartPoseData) {
    super(GeodesicDisplays.SE2C_SE2, gokartPoseData);
    this.gokartPoseData = gokartPoseData;
    timerFrame.geometricComponent.setModel2Pixel(GokartPoseDatas.HANGAR_MODEL2PIXEL);
    {
      spinnerLabelSkips.setList(Arrays.asList(1, 2, 5, 10, 25, 50, 100, 250, 500));
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
      spinnerLabelScheme.setValue(HermiteSubdivisions.HERMITE3);
      spinnerLabelScheme.addToComponentReduced(timerFrame.jToolBar, new Dimension(140, 28), "scheme");
    }
    {
      JTextField jTextField = new JTextField(6);
      jTextField.setText(HermiteSubdivisions.LAMBDA.toString());
      jTextField.addActionListener(e -> {
        try {
          Scalar scalar = Scalars.fromString(jTextField.getText());
          if (NumberQ.of(scalar))
            HermiteSubdivisions.LAMBDA = scalar;
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      });
      jTextField.setPreferredSize(new Dimension(40, 28));
      timerFrame.jToolBar.add(jTextField);
    }
    {
      JTextField jTextField = new JTextField(6);
      jTextField.setText(HermiteSubdivisions.MU.toString());
      jTextField.addActionListener(e -> {
        try {
          Scalar scalar = Scalars.fromString(jTextField.getText());
          if (NumberQ.of(scalar))
            HermiteSubdivisions.MU = scalar;
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      });
      jTextField.setPreferredSize(new Dimension(40, 28));
      timerFrame.jToolBar.add(jTextField);
    }
    {
      JTextField jTextField = new JTextField(6);
      jTextField.setText(HermiteSubdivisions.THETA.toString());
      jTextField.addActionListener(e -> {
        try {
          Scalar scalar = Scalars.fromString(jTextField.getText());
          if (NumberQ.of(scalar))
            HermiteSubdivisions.THETA = scalar;
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      });
      jTextField.setPreferredSize(new Dimension(40, 28));
      timerFrame.jToolBar.add(jTextField);
    }
    {
      JTextField jTextField = new JTextField(6);
      jTextField.setText(HermiteSubdivisions.OMEGA.toString());
      jTextField.addActionListener(e -> {
        try {
          Scalar scalar = Scalars.fromString(jTextField.getText());
          if (NumberQ.of(scalar))
            HermiteSubdivisions.OMEGA = scalar;
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      });
      jTextField.setPreferredSize(new Dimension(40, 28));
      timerFrame.jToolBar.add(jTextField);
    }
    {
      spinnerLabelLevel.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
      spinnerLabelLevel.setValue(3);
      spinnerLabelLevel.addToComponentReduced(timerFrame.jToolBar, new Dimension(40, 28), "level");
      // spinnerLabelLevel.addSpinnerListener(type -> updateState());
    }
    timerFrame.jToolBar.addSeparator();
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
    control.set(new So2Lift(), Tensor.ALL, 0, 2);
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
    RenderQuality.setQuality(graphics);
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    {
      final Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(1));
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
    HermiteSubdivision hermiteSubdivision = spinnerLabelScheme.getValue().supply( //
        geodesicDisplay.hsManifold(), //
        geodesicDisplay.hsTransport(), //
        geodesicDisplay.biinvariantMean());
    TensorIteration tensorIteration = hermiteSubdivision.string(delta, _control);
    int levels = spinnerLabelLevel.getValue();
    Tensor refined = Do.of(_control, tensorIteration::iterate, levels);
    pathRenderShape.setCurve(refined.get(Tensor.ALL, 0), false).render(geometricLayer, graphics);
    new Se2HermitePlot(refined, RealScalar.of(0.3)).render(geometricLayer, graphics);
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
  }

  public static void main(String[] args) {
    new HermiteDatasetDemo(GokartPoseDataV2.RACING_DAY).setVisible(1000, 800);
  }
}
