// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.JToggleButton;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.Variograms;
import ch.ethz.idsc.sophus.krg.PseudoDistances;
import ch.ethz.idsc.tensor.DeterminateScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualSet;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ abstract class A1KrigingDemo extends ControlPointsDemo {
  private static final int WIDTH = 480;
  private static final int HEIGHT = 360;
  static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  // ---
  final SpinnerLabel<PseudoDistances> spinnerDistances = SpinnerLabel.of(PseudoDistances.values());
  final SpinnerLabel<Variograms> spinnerVariogram = SpinnerLabel.of(Variograms.values());
  private final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();
  private final JToggleButton jToggleButton = new JToggleButton("varplot");

  public A1KrigingDemo(GeodesicDisplay geodesicDisplay) {
    super(true, Arrays.asList(geodesicDisplay));
    spinnerDistances.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "pseudo distances");
    spinnerVariogram.setValue(Variograms.POWER);
    spinnerVariogram.addToComponentReduced(timerFrame.jToolBar, new Dimension(250, 28), "variograms");
    {
      spinnerBeta.setList(Tensors.fromString("{1, 17/16, 9/8, 5/4, 3/2, 1.75, 1.99, 2, 3}").stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setIndex(0);
      spinnerBeta.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "beta");
    }
    jToggleButton.setSelected(true);
    timerFrame.jToolBar.add(jToggleButton);
  }

  final ScalarUnaryOperator variogram() {
    Scalar beta = spinnerBeta.getValue();
    return spinnerVariogram.getValue().of(beta);
  }

  final boolean isDeterminate() {
    return DeterminateScalarQ.of(variogram().apply(RealScalar.ZERO));
  }

  @Override
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    protected_render(geometricLayer, graphics);
    RenderQuality.setDefault(graphics);
    if (jToggleButton.isSelected()) {
      ScalarUnaryOperator variogram = variogram();
      VisualSet visualSet = new VisualSet();
      Tensor domain = Subdivide.of(isDeterminate() ? 0.0 : 0.1, 3.0, 100);
      visualSet.add(domain, domain.map(variogram));
      JFreeChart jFreeChart = ListPlot.of(visualSet);
      Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
      jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
    }
    renderControlPoints(geometricLayer, graphics);
  }

  abstract void protected_render(GeometricLayer geometricLayer, Graphics2D graphics);
}
