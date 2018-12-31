// code by ynager
package ch.ethz.idsc.owl.mapping;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;

import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

public abstract class ShadowMapCV implements ShadowMapInterface, RenderInterface {
  protected final LidarEmulator lidarEmulator;
  protected final Tensor pixel2world;
  protected final Tensor world2pixel;
  protected final GeometricLayer world2pixelLayer;
  protected final BufferedImage bufferedImage;
  // ---
  public Color colorShadowFill;
  public final Scalar pixelDim;

  public ShadowMapCV(LidarEmulator lidarEmulator, ImageRegion imageRegion) {
    this.lidarEmulator = lidarEmulator;
    bufferedImage = RegionRenders.image(imageRegion.image());
    Tensor scale = imageRegion.scale();
    Scalar height = RealScalar.of(bufferedImage.getHeight());
    pixelDim = scale.Get(0).reciprocal(); // meters per pixel
    world2pixel = DiagonalMatrix.of(scale.Get(0), scale.Get(1).negate(), RealScalar.ONE);
    world2pixel.set(height, 1, 2);
    pixel2world = DiagonalMatrix.of( //
        scale.Get(0).reciprocal(), scale.Get(1).negate().reciprocal(), RealScalar.ONE);
    pixel2world.set(height.multiply(scale.Get(1).reciprocal()), 1, 2);
    world2pixelLayer = GeometricLayer.of(world2pixel);
  }

  @Override // from ShadowMapInterface
  public final Point state2pixel(Tensor state) {
    Point2D point2D = world2pixelLayer.toPoint2D(state);
    return new Point( //
        (int) point2D.getX(), //
        (int) point2D.getY());
  }

  public final void setColor(Color color) {
    colorShadowFill = color;
  }

  public abstract void updateMap(Mat mat, StateTime stateTime, float timeDelta);

  public abstract Mat getShape(Mat mat, float radius);
}
