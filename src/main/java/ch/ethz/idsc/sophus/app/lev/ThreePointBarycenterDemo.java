// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Optional;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerListener;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.H2Display;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gds.R2Display;
import ch.ethz.idsc.sophus.gds.S2Display;
import ch.ethz.idsc.sophus.opt.PolygonCoordinates;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

/* package */ class ThreePointBarycenterDemo extends LogWeightingDemo implements SpinnerListener<ManifoldDisplay> {
  private final JToggleButton jToggleNeutral = new JToggleButton("neutral");

  public ThreePointBarycenterDemo() {
    super(true, GeodesicDisplays.R2_H2_S2, Arrays.asList(PolygonCoordinates.values()));
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
      leversRender.renderSurfaceP();
      leversRender.renderSequence();
      leversRender.renderTangentsXtoP(false);
      leversRender.renderPolygonXtoP();
      leversRender.renderLevers();
      leversRender.renderIndexX();
      leversRender.renderIndexP();
      try {
        TensorUnaryOperator tensorUnaryOperator = operator(sequence);
        Tensor weights = tensorUnaryOperator.apply(origin);
        leversRender.renderWeights(weights);
        BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
        Tensor mean = biinvariantMean.mean(sequence, weights);
        LeversRender.ORIGIN_RENDER_0 //
            .show(geodesicDisplay::matrixLift, geodesicDisplay.shape(), Tensors.of(mean)) //
            .render(geometricLayer, graphics);
      } catch (Exception e) {
        System.err.println(e);
      }
    } else {
      renderControlPoints(geometricLayer, graphics);
    }
  }

  @Override
  public void actionPerformed(ManifoldDisplay geodesicDisplay) {
    if (geodesicDisplay instanceof R2Display) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.175, 0.358, 0.000}, {-0.991, 0.113, 0.000}, {-0.644, 0.967, 0.000}, {0.509, 0.840, 0.000}, {0.689, 0.513, 0.000}, {0.956, -0.627, 0.000}}"));
    } else //
    if (geodesicDisplay instanceof H2Display) {
      setControlPointsSe2(Tensors.fromString( //
          "{{0.200, 0.233, 0.000}, {-0.867, 2.450, 0.000}, {2.300, 2.117, 0.000}, {2.567, 0.150, 0.000}, {1.600, -2.583, 0.000}, {-2.550, -1.817, 0.000}}"));
    } else //
    if (geodesicDisplay instanceof S2Display) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.363, 0.388, 0.000}, {-0.825, -0.271, 0.000}, {-0.513, 0.804, 0.000}, {0.646, 0.667, 0.000}, {0.704, -0.100, 0.000}, {-0.075, -0.733, 0.000}}"));
    }
  }

  public static void main(String[] args) {
    new ThreePointBarycenterDemo().setVisible(1200, 900);
  }
}
