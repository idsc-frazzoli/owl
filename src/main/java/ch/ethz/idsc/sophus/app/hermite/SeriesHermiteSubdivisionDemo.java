// code by jph
package ch.ethz.idsc.sophus.app.hermite;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.Curvature2DRender;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.math.Do;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.sophus.opt.HermiteSubdivisions;
import ch.ethz.idsc.sophus.ref.d1h.HermiteSubdivision;
import ch.ethz.idsc.tensor.NumberQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.num.Derive;
import ch.ethz.idsc.tensor.num.Series;
import ch.ethz.idsc.tensor.sca.N;

/* package */ class SeriesHermiteSubdivisionDemo extends ControlPointsDemo implements ActionListener {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 360;
  // ---
  private final SpinnerLabel<HermiteSubdivisions> spinnerLabelScheme = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleButton = new JToggleButton("derivatives");
  private final JTextField jTextField = new JTextField(30);

  public SeriesHermiteSubdivisionDemo() {
    super(false, GeodesicDisplays.R2_ONLY);
    setPositioningEnabled(false);
    setMidpointIndicated(false);
    // ---
    {
      spinnerLabelScheme.setArray(HermiteSubdivisions.values());
      spinnerLabelScheme.setValue(HermiteSubdivisions.HERMITE1);
      spinnerLabelScheme.addToComponentReduced(timerFrame.jToolBar, new Dimension(140, 28), "scheme");
    }
    {
      spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
      spinnerRefine.setValue(6);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    }
    timerFrame.jToolBar.addSeparator();
    {
      jToggleButton.setSelected(true);
      jToggleButton.setToolTipText("show derivatives");
      timerFrame.jToolBar.add(jToggleButton);
    }
    {
      jTextField.setText("{2, 1, -1/5, -1/10}");
      jTextField.setPreferredSize(new Dimension(200, 27));
      jTextField.addActionListener(this);
      timerFrame.jToolBar.add(jTextField);
      actionPerformed(null);
    }
  }

  Tensor _control = Tensors.empty();

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    renderControlPoints(geometricLayer, graphics);
    if (1 < _control.length()) {
      ManifoldDisplay geodesicDisplay = manifoldDisplay();
      HermiteSubdivision hermiteSubdivision = //
          spinnerLabelScheme.getValue().supply( //
              geodesicDisplay.hsManifold(), //
              geodesicDisplay.hsTransport(), //
              geodesicDisplay.biinvariantMean());
      Tensor control = N.DOUBLE.of(_control);
      TensorIteration tensorIteration = hermiteSubdivision.string(RealScalar.ONE, control);
      int levels = spinnerRefine.getValue();
      Tensor iterate = Do.of(control, tensorIteration::iterate, levels);
      Tensor curve = Tensor.of(iterate.get(Tensor.ALL, 0).stream().map(Extract2D.FUNCTION));
      Curvature2DRender.of(curve, false, geometricLayer, graphics);
      // ---
      if (jToggleButton.isSelected()) {
        Tensor deltas = iterate.get(Tensor.ALL, 1);
        if (0 < deltas.length()) {
          JFreeChart jFreeChart = StaticHelper.listPlot(deltas);
          Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
          jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
        }
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String string = jTextField.getText();
    Tensor coeffs = Tensors.fromString(string);
    if (VectorQ.of(coeffs) && //
        NumberQ.all(coeffs)) {
      ScalarUnaryOperator f0 = Series.of(coeffs);
      ScalarUnaryOperator f1 = Series.of(Derive.of(coeffs));
      Tensor vx0 = Range.of(-4, 5);
      Tensor vd0 = vx0.map(f0);
      Tensor vx1 = ConstantArray.of(RealScalar.ONE, vx0.length());
      Tensor vd1 = vx0.map(f1);
      Tensor p0 = Transpose.of(Tensors.of(vx0, vd0));
      Tensor p1 = Transpose.of(Tensors.of(vx1, vd1));
      _control = Transpose.of(Tensors.of(p0, p1));
      setControlPointsSe2(Tensor.of(p0.stream().map(Tensor::copy).map(r -> r.append(RealScalar.ZERO))));
    }
  }

  public static void main(String[] args) {
    new SeriesHermiteSubdivisionDemo().setVisible(1200, 600);
  }
}
