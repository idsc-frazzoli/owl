// code by jph
package ch.ethz.idsc.owl.bot.rn;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.CirclePoints;

public class RnPointcloudRegionRender implements RenderInterface {
  private static final int RESOLUTION = 16;
  // ---
  private final Tensor points;
  private final Tensor polygon;

  public RnPointcloudRegionRender(RnPointcloudRegion rnPointcloudRegion) {
    points = rnPointcloudRegion.points();
    Scalar radius = rnPointcloudRegion.radius();
    polygon = CirclePoints.of(RESOLUTION).multiply(radius);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(RegionRenders.COLOR);
    for (Tensor center : points) {
      Path2D path2D = geometricLayer.toPath2D(Tensor.of(polygon.stream().map(row -> row.add(center))));
      path2D.closePath();
      graphics.setColor(RegionRenders.COLOR);
      graphics.fill(path2D);
      graphics.setColor(RegionRenders.BOUNDARY);
      graphics.draw(path2D);
    }
  }
}
