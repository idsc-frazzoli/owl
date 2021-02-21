// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.BufferedImageSupplier;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.CurveVisualSet;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualSet;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

/** class is used in other projects outside of owl */
public abstract class AbstractCurvatureDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final int WIDTH = 640;
  private static final int HEIGHT = 360;
  // ---
  private final JToggleButton jToggleGraph = new JToggleButton("graph");
  public final JToggleButton jToggleCurvature = new JToggleButton("crvtp");

  public AbstractCurvatureDemo() {
    this(GeodesicDisplays.ALL);
  }

  public AbstractCurvatureDemo(List<ManifoldDisplay> list) {
    super(true, list);
    // ---
    jToggleCurvature.setSelected(true);
    jToggleCurvature.setToolTipText("curvature plot");
    timerFrame.jToolBar.add(jToggleCurvature);
    // ---
    if (this instanceof BufferedImageSupplier) {
      jToggleGraph.setSelected(true);
      timerFrame.jToolBar.add(jToggleGraph);
    }
  }

  @Override
  public synchronized final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    Tensor refined = protected_render(geometricLayer, graphics);
    if (this instanceof BufferedImageSupplier && //
        jToggleGraph.isSelected()) {
      BufferedImageSupplier bufferedImageSupplier = (BufferedImageSupplier) this;
      graphics.drawImage(bufferedImageSupplier.bufferedImage(), 0, 0, null);
    }
    if (jToggleCurvature.isSelected() && //
        1 < refined.length()) {
      Tensor tensor = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
      VisualSet visualSet = new VisualSet(COLOR_DATA_INDEXED);
      CurveVisualSet curveVisualSet = new CurveVisualSet(tensor);
      curveVisualSet.addCurvature(visualSet);
      Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
      ListPlot.of(visualSet).draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
    }
  }

  protected abstract Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics);
}
