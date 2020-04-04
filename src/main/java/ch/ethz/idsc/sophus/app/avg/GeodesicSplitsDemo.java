// code by jph
package ch.ethz.idsc.sophus.app.avg;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Objects;
import java.util.stream.IntStream;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.sym.SymLink;
import ch.ethz.idsc.sophus.app.sym.SymLinkBuilder;
import ch.ethz.idsc.sophus.app.sym.SymLinkImage;
import ch.ethz.idsc.sophus.app.sym.SymScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ abstract class GeodesicSplitsDemo extends ControlPointsDemo {
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 13);

  public GeodesicSplitsDemo() {
    super(true, GeodesicDisplays.ALL);
  }

  @Override // from RenderInterface
  public synchronized final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor control = getGeodesicControlPoints();
    // ---
    SymScalar symScalar = symScalar(Tensor.of(IntStream.range(0, control.length()).mapToObj(SymScalar::leaf)));
    SymLink symLink = null;
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    if (Objects.nonNull(symScalar)) {
      graphics.drawImage(new SymLinkImage(symScalar, FONT).bufferedImage(), 0, 0, null);
      // ---
      symLink = SymLinkBuilder.of(control, symScalar);
      // ---
      RenderQuality.setQuality(graphics);
      GeometricSymLinkRender.of(geodesicDisplay, symLink).render(geometricLayer, graphics);
      RenderQuality.setDefault(graphics);
    }
    renderControlPoints(geometricLayer, graphics);
    // ---
    if (Objects.nonNull(symLink)) {
      Tensor xya = symLink.getPosition(geodesicDisplay.geodesicInterface());
      renderPoints(geodesicDisplay, Tensors.of(xya), geometricLayer, graphics);
    }
  }

  /** evaluates geodesic average on symbolic leaf sequence
   * 
   * @param vector of length at least 1
   * @return null if computation of geodesic average is not defined for given vector */
  abstract SymScalar symScalar(Tensor vector);
}
