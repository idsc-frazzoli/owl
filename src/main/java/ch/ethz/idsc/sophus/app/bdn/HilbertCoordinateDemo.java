// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PolygonCoordinates;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.sophus.hs.r2.HilbertPolygon;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.sca.Power;

/* package */ class HilbertCoordinateDemo extends ExportWeightingDemo {
  public static Tensor standardized(int n) {
    Tensor polygon = HilbertPolygon.of(n).multiply(Power.of(2.0, -n + 1));
    return polygon.map(scalar -> scalar.subtract(RealScalar.of(1.0 + 1e-5)));
  }

  public HilbertCoordinateDemo() {
    super(false, GeodesicDisplays.R2_ONLY, PolygonCoordinates.list());
    setPositioningEnabled(false);
    setMidpointIndicated(false);
    // ---
    Tensor polygon = standardized(2);
    polygon = PadRight.zeros(polygon.length(), 3).apply(polygon);
    setControlPointsSe2(polygon);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    final Tensor sequence = getGeodesicControlPoints();
    LeversRender leversRender = //
        LeversRender.of(geodesicDisplay(), sequence, null, geometricLayer, graphics);
    leversRender.renderIndexX();
    leversRender.renderIndexP();
    leversRender.renderSurfaceP();
    BufferedImage bufferedImage = StaticHelper.levelsImage(geodesicDisplay(), sequence, refinement(), colorDataGradient(), 32);
    graphics.drawImage(bufferedImage, 0, 200, bufferedImage.getWidth() * magnification(), bufferedImage.getHeight() * magnification(), null);
  }

  public static void main(String[] args) {
    new HilbertCoordinateDemo().setVisible(1300, 900);
  }
}
