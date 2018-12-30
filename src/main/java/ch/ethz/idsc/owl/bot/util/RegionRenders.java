// code by jph
package ch.ethz.idsc.owl.bot.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.bot.rn.RnPointcloudRegion;
import ch.ethz.idsc.owl.bot.rn.RnPointcloudRegionRender;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.region.EllipseRegionRender;
import ch.ethz.idsc.owl.gui.region.ImageRegionRender;
import ch.ethz.idsc.owl.gui.region.PolygonRegionRender;
import ch.ethz.idsc.owl.gui.region.StateTimeCollectorRender;
import ch.ethz.idsc.owl.gui.ren.ConeRegionRender;
import ch.ethz.idsc.owl.gui.ren.SphericalRegionRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.planar.ConeRegion;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.PolygonRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.StateTimeCollector;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.io.ImageFormat;

public enum RegionRenders {
  ;
  /** raster value 230 get's mapped to color {244, 244, 244, 255}
   * when using getRGB because of the color model attached to the
   * image type grayscale */
  public static final int RGB = 230;
  /** default color for obstacle region */
  public static final Color COLOR = new Color(RGB, RGB, RGB);
  public static final Color BOUNDARY = new Color(192, 192, 192);
  // ---
  private static final Scalar TFF = RealScalar.of(255);
  private static final Scalar OBS = RealScalar.of(RGB);

  static Scalar color(Scalar scalar) {
    return Scalars.isZero(scalar) ? TFF : OBS;
  }

  /** @param image with rank 2
   * @return */
  public static BufferedImage image(Tensor image) {
    return ImageFormat.of(MatrixQ.require(image).map(RegionRenders::color));
  }

  /** @param region
   * @return new instance of {@link RenderInterface} that visualizes given region,
   * or null if drawing capability is not available for the region */
  public static RenderInterface create(Region<Tensor> region) {
    if (region instanceof ImageRegion)
      return ImageRegionRender.create((ImageRegion) region);
    if (region instanceof EllipsoidRegion)
      return EllipseRegionRender.of((EllipsoidRegion) region);
    if (region instanceof SphericalRegion)
      return EllipseRegionRender.of((SphericalRegion) region);
    if (region instanceof PolygonRegion)
      return new PolygonRegionRender((PolygonRegion) region);
    if (region instanceof RnPointcloudRegion)
      return new RnPointcloudRegionRender((RnPointcloudRegion) region);
    throw new RuntimeException();
  }

  public static RenderInterface create(TrajectoryRegionQuery trajectoryRegionQuery) {
    if (trajectoryRegionQuery instanceof StateTimeCollector)
      return new StateTimeCollectorRender((StateTimeCollector) trajectoryRegionQuery);
    throw new RuntimeException();
  }

  public static void draw(GeometricLayer geometricLayer, Graphics2D graphics, Region<Tensor> region) {
    if (region instanceof ConeRegion)
      ConeRegionRender.draw(geometricLayer, graphics, (ConeRegion) region);
    if (region instanceof SphericalRegion)
      SphericalRegionRender.draw(geometricLayer, graphics, (SphericalRegion) region);
  }
}
