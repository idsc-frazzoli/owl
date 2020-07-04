// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerListener;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.Spd2GeodesicDisplay;
import ch.ethz.idsc.sophus.krg.Biinvariant;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ class WeightsDemo extends AbstractPlaceDemo implements SpinnerListener<GeodesicDisplay> {
  private static final Biinvariant[] BIINVARIANTS = //
      { Biinvariant.METRIC, Biinvariant.TARGET, Biinvariant.HARBOR, Biinvariant.GARDEN };
  private final JToggleButton jToggleAxes = new JToggleButton("axes");

  public WeightsDemo() {
    super(GeodesicDisplays.MANIFOLDS, LogWeightings.list());
    {
      timerFrame.jToolBar.add(jToggleAxes);
    }
    setControlPointsSe2(Tensors.fromString("{{-1, -2, 0}, {3, -2, -1}, {4, 2, 1}, {-1, 3, 2}, {-2, -3, -2}}"));
    GeodesicDisplay geodesicDisplay = S2GeodesicDisplay.INSTANCE;
    setGeodesicDisplay(geodesicDisplay);
    actionPerformed(geodesicDisplay);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor controlPointsAll = getGeodesicControlPoints();
    if (0 < controlPointsAll.length()) {
      Tensor sequence = controlPointsAll.extract(1, controlPointsAll.length());
      Tensor origin = controlPointsAll.get(0);
      LeversRender leversRender = LeversRender.of( //
          geodesicDisplay, //
          null, //
          sequence, //
          origin, geometricLayer, graphics);
      leversRender.renderLevers();
      leversRender.renderOrigin();
      leversRender.renderSequence();
      // ---
      if (geodesicDisplay.dimensions() < sequence.length()) {
        Tensor matrix = Tensors.empty();
        for (Biinvariant biinvariant : BIINVARIANTS) {
          TensorUnaryOperator tensorUnaryOperator = //
              biinvariant.coordinate(geodesicDisplay.vectorLogManifold(), variogram(), sequence);
          Tensor weights = tensorUnaryOperator.apply(origin);
          matrix.append(weights);
        }
        // ---
        ColorDataIndexed colorDataIndexed = ColorDataLists._097.strict();
        for (int index = 0; index < sequence.length(); ++index) {
          Tensor map = matrix.get(Tensor.ALL, index).map(Tensors::of);
          leversRender.renderMatrix(index, map, colorDataIndexed);
        }
        int index = 0;
        graphics.setFont(LeversRender.FONT_MATRIX);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int fheight = fontMetrics.getAscent();
        for (Biinvariant biinvariant : BIINVARIANTS) {
          graphics.setColor(colorDataIndexed.getColor(index));
          graphics.drawString(biinvariant.title(), 2, (index + 1) * fheight);
          ++index;
        }
      }
    }
  }

  @Override
  public void actionPerformed(GeodesicDisplay geodesicDisplay) {
    if (geodesicDisplay instanceof S2GeodesicDisplay) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.346, -0.096, 0.262}, {-0.113, 0.858, 0.000}, {0.721, 0.288, -0.262}, {0.171, -0.038, 0.262}, {0.429, -0.646, -0.262}, {-0.804, -0.446, 0.524}, {-0.829, 0.513, -0.262}}"));
    }
    if (geodesicDisplay instanceof Spd2GeodesicDisplay) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.325, -0.125, 1.309}, {-0.708, 1.475, -3.927}, {1.942, 1.075, -1.309}, {-0.308, -0.825, 4.974}, {-2.292, -0.608, 0.524}, {2.042, -0.625, -4.189}, {-4.108, 0.325, 1.309}}"));
    }
  }

  public static void main(String[] args) {
    new WeightsDemo().setVisible(1200, 600);
  }
}
