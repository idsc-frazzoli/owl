// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Optional;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.java.awt.SpinnerListener;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gds.R2Display;
import ch.ethz.idsc.sophus.gds.S2Display;
import ch.ethz.idsc.sophus.gds.Se2AbstractDisplay;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;

/* package */ class AffineDemo extends AbstractPlaceDemo implements SpinnerListener<ManifoldDisplay> {
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = SpinnerLabel.of(ColorDataGradients.values());
  private final JToggleButton jToggleNeutral = new JToggleButton("neutral");

  public AffineDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    // ---
    spinnerColorData.setValue(ColorDataGradients.TEMPERATURE);
    spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "color scheme");
    // ---
    timerFrame.jToolBar.add(jToggleNeutral);
    // ---
    ManifoldDisplay geodesicDisplay = R2Display.INSTANCE;
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
      leversRender.renderSequence();
      leversRender.renderOrigin();
      // Tensor weights = AffineCoordinate.of(vectorLogManifold).weights(sequence, origin);
      // leversRender.renderWeights(weights);
      Tensor doma = Subdivide.of(0.0, 1.0, 11);
      for (int index = 0; index < sequence.length(); ++index) {
        Tensor prev = sequence.get(Math.floorMod(index - 1, sequence.length()));
        Tensor next = sequence.get(index);
        Tensor curve = doma.map(geodesicDisplay.geodesicInterface().curve(prev, next));
        Tensor polygon = Tensor.of(curve.stream().map(geodesicDisplay::toPoint));
        Path2D path2d = geometricLayer.toPath2D(polygon);
        graphics.draw(path2d);
      }
    } else {
      renderControlPoints(geometricLayer, graphics);
    }
  }

  @Override
  public void actionPerformed(ManifoldDisplay geodesicDisplay) {
    if (geodesicDisplay instanceof R2Display) {
      setControlPointsSe2(Tensors.fromString("{{0.3, 1, 0}, {0, 0, 0}, {1, 0, 0}}"));
    } else //
    if (geodesicDisplay instanceof S2Display) {
      setControlPointsSe2(Tensors.fromString( //
          "{{0.300, 0.092, 0.000}, {-0.563, -0.658, 0.262}, {-0.854, -0.200, 0.000}, {-0.746, 0.663, -0.262}, {0.467, 0.758, 0.262}, {0.446, -0.554, 0.262}}"));
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.521, 0.621, 0.262}, {-0.863, 0.258, 0.000}, {-0.725, 0.588, -0.785}, {0.392, 0.646, 0.000}, {-0.375, 0.021, 0.000}, {-0.525, -0.392, 0.000}}"));
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.583, 0.338, 0.000}, {-0.904, -0.258, 0.262}, {-0.513, 0.804, 0.000}, {0.646, 0.667, 0.000}, {0.704, -0.100, 0.000}, {0.396, -0.688, 0.000}}"));
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.363, 0.388, 0.000}, {-0.825, -0.271, 0.000}, {-0.513, 0.804, 0.000}, {0.646, 0.667, 0.000}, {0.704, -0.100, 0.000}, {-0.075, -0.733, 0.000}}"));
    } else //
    if (geodesicDisplay instanceof Se2AbstractDisplay) {
      setControlPointsSe2(Tensors.fromString(
          "{{3.150, -2.700, -0.524}, {-1.950, -3.683, 0.000}, {-1.500, -1.167, 2.094}, {4.533, -0.733, -1.047}, {8.567, -3.300, -1.309}, {2.917, -5.050, -1.047}}"));
    }
  }

  public static void main(String[] args) {
    new AffineDemo().setVisible(1200, 900);
  }
}
