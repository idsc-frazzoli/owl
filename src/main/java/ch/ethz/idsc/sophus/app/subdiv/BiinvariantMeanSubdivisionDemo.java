// code by jph
package ch.ethz.idsc.sophus.app.subdiv;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.misc.Curvature2DRender;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.MSpline3CurveSubdivision;
import ch.ethz.idsc.sophus.math.MidpointInterface;
import ch.ethz.idsc.tensor.Tensor;

public class BiinvariantMeanSubdivisionDemo extends AbstractCurveSubdivisionDemo {
  public BiinvariantMeanSubdivisionDemo() {
    super(GeodesicDisplays.SE2C_SE2_R2);
  }

  @Override
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final CurveSubdivisionSchemes scheme = spinnerLabel.getValue();
    // ---
    if (jToggleSymi.isSelected()) {
      Optional<SymMaskImages> optional = SymMaskImages.get(scheme.name());
      if (optional.isPresent()) {
        BufferedImage image0 = optional.get().image0();
        graphics.drawImage(image0, 0, 0, null);
        BufferedImage image1 = optional.get().image1();
        graphics.drawImage(image1, image0.getWidth() + 1, 0, null);
      }
    }
    GraphicsUtil.setQualityHigh(graphics);
    // ---
    final boolean cyclic = jToggleCyclic.isSelected() || !scheme.isStringSupported();
    Tensor control = getGeodesicControlPoints();
    int levels = spinnerRefine.getValue();
    renderControlPoints(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    MidpointInterface midpointInterface = geodesicDisplay.geodesicInterface();
    // TODO JPH implement other curve subdivision schemes using biinvariant mean
    CurveSubdivision curveSubdivision = new MSpline3CurveSubdivision(geodesicDisplay.biinvariantMean());
    Tensor refined = StaticHelper.refine(control, levels, curveSubdivision, //
        CurveSubdivisionHelper.isDual(scheme), cyclic, midpointInterface);
    Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
    Curvature2DRender.of(render, cyclic, geometricLayer, graphics);
    if (levels < 5)
      renderPoints(geodesicDisplay, refined, geometricLayer, graphics);
    return refined;
  }

  public static void main(String[] args) {
    new BiinvariantMeanSubdivisionDemo().setVisible(1200, 800);
  }
}
