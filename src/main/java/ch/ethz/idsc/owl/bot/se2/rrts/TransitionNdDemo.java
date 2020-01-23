// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** this demo maps the GeodesicDisplays
 * 
 * Clothoid -> ClothoidTransitionSpace
 * SE2 -> DubinsTransitionSpace
 * R2 -> RnTransitionSpace
 * 
 * this design is not extendable!
 * do not reproduce this design! */
public class TransitionNdDemo extends ControlPointsDemo {
  private static final Tensor LBOUNDS = Tensors.vector(-5, -5).unmodifiable();
  private static final Tensor UBOUNDS = Tensors.vector(+5, +5).unmodifiable();
  // ---
  private final SpinnerLabel<Integer> spinnerTotal = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerValue = new SpinnerLabel<>();
  // ---
  private TransitionNdContainer transitionNdContainer;

  public TransitionNdDemo() {
    super(false, GeodesicDisplays.CLOTH_SE2_R2);
    setPositioningEnabled(false);
    setMidpointIndicated(false);
    // ---
    spinnerTotal.setList(Arrays.asList(5, 10, 20, 50, 100, 200, 500, 1000, 2000));
    spinnerTotal.setValue(20);
    spinnerTotal.addSpinnerListener(this::config);
    spinnerTotal.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "total");
    // ---
    spinnerValue.setList(Arrays.asList(1, 2, 3, 4, 5, 10, 20, 50));
    spinnerValue.setValue(3);
    spinnerValue.addSpinnerListener(this::config);
    spinnerValue.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    config(spinnerTotal.getValue());
  }

  /** @param not used */
  private void config(int not) {
    transitionNdContainer = new TransitionNdContainer( //
        LBOUNDS, UBOUNDS, //
        spinnerTotal.getValue(), //
        spinnerValue.getValue());
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    transitionNdContainer.render( //
        geodesicDisplay, //
        geometricLayer, //
        graphics, //
        geodesicDisplay.project(geometricLayer.getMouseSe2State()));
  }

  public static void main(String[] args) {
    new TransitionNdDemo().setVisible(1200, 800);
  }
}
