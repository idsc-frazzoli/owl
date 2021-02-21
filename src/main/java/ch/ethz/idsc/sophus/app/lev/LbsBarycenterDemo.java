// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Optional;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerListener;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplayRender;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gds.S2Display;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.opt.PolygonCoordinates;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** Visualization of
 * "Spherical Barycentric Coordinates"
 * by Torsten Langer, Alexander Belyaev, Hans-Peter Seidel, 2005 */
/* package */ class LbsBarycenterDemo extends LogWeightingDemo implements SpinnerListener<ManifoldDisplay> {
  private final JToggleButton jToggleNeutral = new JToggleButton("neutral");

  public LbsBarycenterDemo() {
    super(true, GeodesicDisplays.S2_ONLY, Arrays.asList(PolygonCoordinates.values()));
    // ---
    timerFrame.jToolBar.add(jToggleNeutral);
    // ---
    ManifoldDisplay geodesicDisplay = S2Display.INSTANCE;
    setGeodesicDisplay(geodesicDisplay);
    actionPerformed(geodesicDisplay);
    addSpinnerListener(this);
    jToggleNeutral.setSelected(true);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    Optional<Tensor> optional = getOrigin();
    if (optional.isPresent()) {
      Tensor sequence = getSequence();
      Tensor origin = optional.get();
      LeversRender leversRender = //
          LeversRender.of(geodesicDisplay, sequence, origin, geometricLayer, graphics);
      // ---
      leversRender.renderSurfaceP();
      leversRender.renderSequence();
      leversRender.renderOrigin();
      leversRender.renderLbsS2();
      leversRender.renderLevers();
      leversRender.renderIndexX();
      leversRender.renderIndexP();
      // ---
      geometricLayer.pushMatrix(Se2Matrix.translation(Tensors.vector(3, 0)));
      GeodesicDisplayRender.render_s2(geometricLayer, graphics);
      // ---
      leversRender.renderSurfaceP();
      leversRender.renderSequence();
      leversRender.renderOrigin();
      leversRender.renderTangentsXtoP(false);
      leversRender.renderPolygonXtoP();
      leversRender.renderLevers();
      leversRender.renderIndexX();
      leversRender.renderIndexP();
      // ---
      geometricLayer.popMatrix();
    } else {
      renderControlPoints(geometricLayer, graphics);
    }
  }

  @Override
  public void actionPerformed(ManifoldDisplay geodesicDisplay) {
    if (geodesicDisplay instanceof S2Display) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.314, 0.662, 0.000}, {-0.809, 0.426, 0.000}, {-0.261, 0.927, 0.000}, {0.564, 0.685, 0.000}, {0.694, 0.220, 0.000}}"));
    }
  }

  public static void main(String[] args) {
    new LbsBarycenterDemo().setVisible(1200, 900);
  }
}
