// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.java.awt.SpinnerListener;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.Se2AbstractGeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.Se2CoveringGeodesicDisplay;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Drop;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class GrassmannDemo extends AbstractPlaceDemo implements SpinnerListener<GeodesicDisplay> {
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = SpinnerLabel.of(ColorDataGradients.values());
  private final JToggleButton jToggleNeutral = new JToggleButton("neutral");
  private final JButton jButtonPrint = new JButton("print");

  public GrassmannDemo() {
    super(GeodesicDisplays.SE2C_SE2_S2_H2_R2, LogWeightings.list());
    // ---
    spinnerColorData.setValue(ColorDataGradients.TEMPERATURE);
    spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "color scheme");
    // ---
    jButtonPrint.addActionListener(l -> System.out.println(getControlPointsSe2().map(Round._3)));
    // ---
    timerFrame.jToolBar.add(jButtonPrint);
    timerFrame.jToolBar.add(jToggleNeutral);
    // ---
    GeodesicDisplay geodesicDisplay = Se2CoveringGeodesicDisplay.INSTANCE;
    setGeodesicDisplay(geodesicDisplay);
    actionPerformed(geodesicDisplay);
    addSpinnerListener(this);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor geodesicControlPoints = getGeodesicControlPoints();
    if (0 < geodesicControlPoints.length()) {
      Tensor sequence = Drop.head(geodesicControlPoints, 1);
      Tensor origin = geodesicControlPoints.get(0);
      TensorUnaryOperator tensorUnaryOperator = jToggleNeutral.isSelected() //
          ? null
          : operator(sequence);
      LeversRender leversRender = LeversRender.of( //
          geodesicDisplay, tensorUnaryOperator, //
          sequence, origin, geometricLayer, graphics);
      ColorDataGradient colorDataGradient = spinnerColorData.getValue().deriveWithOpacity(RealScalar.of(0.5));
      LeversHud.render(pseudoDistances(), leversRender, colorDataGradient);
    } else {
      renderControlPoints(geometricLayer, graphics);
    }
  }

  @Override
  public void actionPerformed(GeodesicDisplay geodesicDisplay) {
    if (geodesicDisplay instanceof S2GeodesicDisplay) {
      setControlPointsSe2(Tensors.fromString(
          "{{-0.521, 0.621, 0.262}, {-0.863, 0.258, 0.000}, {-0.725, 0.588, -0.785}, {0.392, 0.646, 0.000}, {-0.375, 0.021, 0.000}, {-0.525, -0.392, 0.000}}"));
    } else //
    if (geodesicDisplay instanceof Se2AbstractGeodesicDisplay) {
      setControlPointsSe2(Tensors.fromString(
          "{{3.150, -2.700, -0.524}, {-1.950, -3.683, 0.000}, {-1.500, -1.167, 2.094}, {4.533, -0.733, -1.047}, {8.567, -3.300, -1.309}, {2.917, -5.050, -1.047}}"));
    }
  }

  public static void main(String[] args) {
    new GrassmannDemo().setVisible(1200, 900);
  }
}
