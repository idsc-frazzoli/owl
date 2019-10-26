// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.misc.Curvature2DRender;
import ch.ethz.idsc.sophus.crv.subdiv.BSpline1CurveSubdivision;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;

/** {@link SplitInterface} */
public class SplitCurveSubdivisionDemo extends CurveSubdivisionDemo {
  private final PathRender pathRender = new PathRender(new Color(0, 255, 0, 128));

  public SplitCurveSubdivisionDemo() {
    super(GeodesicDisplays.ALL);
  }

  @Override
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final CurveSubdivisionSchemes scheme = spinnerLabel.getValue();
    if (scheme.equals(CurveSubdivisionSchemes.DODGSON_SABIN))
      setGeodesicDisplay(R2GeodesicDisplay.INSTANCE);
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
    GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    Tensor refined = StaticHelper.refine( //
        control, levels, spinnerLabel.getValue().of(geodesicInterface), //
        CurveSubdivisionHelper.isDual(scheme), cyclic, geodesicInterface);
    if (jToggleLine.isSelected()) {
      TensorUnaryOperator tensorUnaryOperator = StaticHelper.create(new BSpline1CurveSubdivision(geodesicDisplay.geodesicInterface()), cyclic);
      pathRender.setCurve(Nest.of(tensorUnaryOperator, control, 8), cyclic).render(geometricLayer, graphics);
    }
    Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
    Curvature2DRender.of(render, cyclic, jToggleComb.isSelected(), geometricLayer, graphics);
    if (levels < 5)
      renderPoints(geodesicDisplay, refined, geometricLayer, graphics);
    return refined;
  }

  public static void main(String[] args) {
    new SplitCurveSubdivisionDemo().setVisible(1200, 800);
  }
}
