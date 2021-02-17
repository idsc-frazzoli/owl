// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JToggleButton;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.lev.LogWeightingDemo;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.opt.LogWeightings;
import ch.ethz.idsc.tensor.DeterminateScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualSet;

/* package */ abstract class AnAveragingDemo extends LogWeightingDemo {
  private static final int WIDTH = 480;
  private static final int HEIGHT = 360;
  // ---
  private final JToggleButton jToggleButton = new JToggleButton("varplot");

  public AnAveragingDemo(List<ManifoldDisplay> geodesicDisplays) {
    super(true, geodesicDisplays, LogWeightings.averagings());
    setMidpointIndicated(false);
    spinnerLogWeighting.addSpinnerListener(v -> recompute());
    // ---
    jToggleButton.setSelected(false);
    timerFrame.jToolBar.add(jToggleButton);
  }

  final boolean isDeterminate() {
    return DeterminateScalarQ.of(variogram().apply(RealScalar.ZERO));
  }

  @Override
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    protected_render(geometricLayer, graphics);
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
