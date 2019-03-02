// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JToggleButton;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.Tensor;

/* package */ abstract class CurveDemo extends ControlPointsDemo {
  private final JToggleButton jToggleCrvt = new JToggleButton("crvt");

  public CurveDemo() {
    super(true, GeodesicDisplays.ALL);
    // ---
    jToggleCrvt.setSelected(false);
    timerFrame.jToolBar.add(jToggleCrvt);
  }

  @Override
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor refined = protected_render(geometricLayer, graphics);
    // ---
    if (jToggleCrvt.isSelected() && 1 < refined.length()) {
      GeodesicDisplay geodesicDisplay = geodesicDisplay();
      CurveVisualSet curveVisualSet = new CurveVisualSet(Tensor.of(refined.stream().map(geodesicDisplay::toPoint)));
      curveVisualSet.addCurvature();
      if (2 < refined.get(0).length())
        curveVisualSet.addArcTan(refined);
      VisualSet visualSet = curveVisualSet.visualSet();
      {
        // {
        // Tensor domain = Range.of(0, phase.length());
        // VisualRow visualRow = visualSet.add(domain, phase);
        // visualRow.setLabel("phase diff");
        // visualRow.setStroke(PLOT_STROKE);
        // }
        // {
        // Tensor domain = Range.of(0, dpntlnXY.length());
        // VisualRow visualRow = visualSet.add(domain, dpntlnXY);
        // visualRow.setLabel("arclen");
        // visualRow.setStroke(PLOT_STROKE);
        // }
        // {
        // Tensor div = phase.pmul(dpntlnXY.map(InvertUnlessZero.FUNCTION));
        // Tensor domain = Range.of(0, div.length());
        // Tensor values = div.multiply(RealScalar.of(-1));
        // VisualRow visualRow = visualSet.add(domain, values);
        // visualRow.setLabel("phase/arclen");
        // visualRow.setStroke(PLOT_STROKE);
        // }
      }
      JFreeChart jFreeChart = ListPlot.of(visualSet);
      jFreeChart.draw(graphics, new Rectangle2D.Double(0, 0, 800, 480));
    }
  }

  abstract Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics);
}
