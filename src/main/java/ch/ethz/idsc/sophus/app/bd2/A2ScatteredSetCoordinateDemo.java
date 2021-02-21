// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import java.awt.Graphics2D;
import java.util.List;
import java.util.Objects;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.opt.LogWeighting;
import ch.ethz.idsc.tensor.Tensor;

/* package */ abstract class A2ScatteredSetCoordinateDemo extends AbstractExportWeightingDemo {
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  // ---
  private RenderInterface renderInterface;

  public A2ScatteredSetCoordinateDemo(List<LogWeighting> array) {
    super(true, GeodesicDisplays.R2_H2_S2, array);
    // ---
    timerFrame.jToolBar.add(jToggleAxes);
    jToggleHeatmap.setVisible(false);
    jToggleArrows.setVisible(false);
    addMouseRecomputation();
  }

  @Override
  protected final void recompute() {
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    Tensor sequence = getGeodesicControlPoints();
    renderInterface = geodesicDisplay.dimensions() < sequence.length() //
        ? arrayPlotRender(sequence, refinement(), operator(sequence), magnification())
        : null;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    {
      final Tensor sequence = getGeodesicControlPoints();
      LeversRender leversRender = //
          LeversRender.of(manifoldDisplay(), sequence, null, geometricLayer, graphics);
      leversRender.renderIndexX();
      leversRender.renderIndexP();
    }
    // ---
    if (Objects.isNull(renderInterface))
      recompute();
    if (Objects.nonNull(renderInterface))
      renderInterface.render(geometricLayer, graphics);
  }
}
