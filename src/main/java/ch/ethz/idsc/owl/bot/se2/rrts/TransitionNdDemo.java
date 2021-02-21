// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.Container;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.tensor.ref.gui.ConfigPanel;

/** this demo maps the GeodesicDisplays
 * 
 * Clothoid -> ClothoidTransitionSpace
 * SE2 -> DubinsTransitionSpace
 * R2 -> RnTransitionSpace
 * 
 * this design is not extendable!
 * do not reproduce this design! */
public class TransitionNdDemo extends ControlPointsDemo {
  private final TransitionNdParam transitionNdParam = new TransitionNdParam();
  // ---
  private TransitionNdContainer transitionNdContainer;

  public TransitionNdDemo() {
    super(false, GeodesicDisplays.CL_SE2_R2);
    setPositioningEnabled(false);
    setMidpointIndicated(false);
    // ---
    ConfigPanel configPanel = ConfigPanel.of(transitionNdParam);
    configPanel.fieldPanels().addUniversalListener(s -> {
      System.out.println("compute udpate: " + s);
      transitionNdContainer = transitionNdParam.config();
    });
    Container container = timerFrame.jFrame.getContentPane();
    container.add("West", configPanel.getFields());
    // ---
    transitionNdContainer = transitionNdParam.config();
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
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
