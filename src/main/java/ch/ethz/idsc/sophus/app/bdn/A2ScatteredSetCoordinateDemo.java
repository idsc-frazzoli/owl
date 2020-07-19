// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Transpose;

/* package */ abstract class A2ScatteredSetCoordinateDemo extends ExportCoordinateDemo {
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  // ---
  private RenderInterface renderInterface;

  public A2ScatteredSetCoordinateDemo( //
      boolean addRemoveControlPoints, //
      List<GeodesicDisplay> list, //
      List<LogWeighting> array) {
    super(addRemoveControlPoints, list, array);
    // ---
    timerFrame.jToolBar.add(jToggleAxes);
    // ---
    MouseAdapter mouseAdapter = new MouseAdapter() { // TODO code redundant to somewhere
      @Override
      public void mousePressed(MouseEvent mouseEvent) {
        switch (mouseEvent.getButton()) {
        case MouseEvent.BUTTON1: // insert point
          if (!isPositioningOngoing())
            recompute();
          break;
        }
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        if (true && isPositioningOngoing()) // TODO
          recompute();
      };
    };
    // ---
    timerFrame.geometricComponent.jComponent.addMouseListener(mouseAdapter);
    timerFrame.geometricComponent.jComponent.addMouseMotionListener(mouseAdapter);
  }

  @Override
  protected final void recompute() {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor sequence = getGeodesicControlPoints();
    if (geodesicDisplay.dimensions() < sequence.length()) { // render basis functions
      Tensor wgs = compute(operator(sequence), refinement());
      List<Integer> dims = Dimensions.of(wgs);
      Tensor wgp = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
      renderInterface = ArrayPlotRender.rescale(wgp, colorDataGradient(), magnification());
    } else
      renderInterface = null;
  }

  @Override
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    final Tensor sequence = getGeodesicControlPoints();
    LeversRender leversRender = //
        LeversRender.of(geodesicDisplay(), sequence, null, geometricLayer, graphics);
    leversRender.renderIndexX();
    leversRender.renderIndexP();
    // ---
    if (Objects.isNull(renderInterface))
      recompute();
    if (Objects.nonNull(renderInterface))
      renderInterface.render(geometricLayer, graphics);
  }
}
