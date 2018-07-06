// code by ynager
package ch.ethz.idsc.owl.mapping;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.GpuMat;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_cudaarithm;
import org.bytedeco.javacpp.opencv_cudafilters;
import org.bytedeco.javacpp.opencv_cudafilters.Filter;
import org.bytedeco.javacpp.opencv_imgproc;

import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.data.img.CvHelper;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

public class ShadowMapSpherical implements ShadowMap, RenderInterface {
  private Color COLOR_SHADOW_FILL;
  // ---
  private final LidarEmulator lidar;
  private final Mat initArea;
  private final Mat shadowArea;
  private final float vMax;
  private final Scalar pixelDim;
  private final GeometricLayer world2pixelLayer;
  private final Tensor world2pixel;
  private final Tensor pixel2world;
  private final Mat ellipseKernel = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_ELLIPSE, //
      new Size(7, 7));
  //
  private GpuMat initAreaGpu;
  private GpuMat shadowAreaGpu;
  private GpuMat lidarMatGpu;

  public ShadowMapSpherical(LidarEmulator lidar, ImageRegion imageRegion, float vMax, float rMin) {
    this.lidar = lidar;
    this.vMax = vMax;
    BufferedImage bufferedImage = RegionRenders.image(imageRegion.image());
    Mat area = CvHelper.bufferedImageToMat(bufferedImage);
    opencv_imgproc.threshold(area, area, 254, 255, opencv_imgproc.THRESH_BINARY_INV); // TODO magic consts
    //
    // convert imageRegion into Area
    Tensor scale = imageRegion.scale(); // pixels per meter
    pixelDim = scale.Get(0).reciprocal(); // meters per pixel
    world2pixel = DiagonalMatrix.of(scale.Get(0), scale.Get(1).negate(), RealScalar.ONE);
    world2pixel.set(RealScalar.of(bufferedImage.getHeight()), 1, 2);
    //
    pixel2world = DiagonalMatrix.of( //
        scale.Get(0).reciprocal(), scale.Get(1).negate().reciprocal(), RealScalar.ONE);
    pixel2world.set(RealScalar.of(bufferedImage.getHeight()).multiply(scale.Get(1).reciprocal()), 1, 2);
    world2pixelLayer = GeometricLayer.of(world2pixel);
    //
    Mat obstacleArea = area.clone();
    initArea = new Mat(obstacleArea.size(), obstacleArea.type(), org.bytedeco.javacpp.opencv_core.Scalar.WHITE);
    opencv_imgproc.erode(initArea, initArea, ellipseKernel, new Point(-1, -1), 1, opencv_core.BORDER_CONSTANT, null);
    opencv_imgproc.dilate(obstacleArea, obstacleArea, ellipseKernel, new Point(-1, -1), radius2it(ellipseKernel, rMin), opencv_core.BORDER_CONSTANT, null);
    opencv_core.subtract(initArea, obstacleArea, initArea);
    this.shadowArea = initArea.clone();
    setColor(new Color(255, 50, 74));
    //
    initAreaGpu = new GpuMat(initArea.size(), initArea.type());
    shadowAreaGpu = new GpuMat(shadowArea.size(), shadowArea.type());
    lidarMatGpu = new GpuMat(initArea.size(), initArea.type());
    shadowAreaGpu.upload(shadowArea);
    initAreaGpu.upload(initArea);
  }

  @Override
  public void updateMap(StateTime stateTime, float timeDelta) {
    // Stopwatch stopwatch = Stopwatch.started();
    // updateMap(shadowArea, stateTime, timeDelta);
    updateMapGpu(shadowAreaGpu, stateTime, timeDelta);
    // System.out.println(stopwatch.display_nanoSeconds() / 1000000.0);
  }

  @Override
  public Point state2pixel(Tensor state) {
    GeometricLayer layer = GeometricLayer.of(world2pixel);
    Point2D point2D = layer.toPoint2D(state);
    return new Point( //
        (int) point2D.getX(), //
        (int) point2D.getY());
  }

  // @Override
  public synchronized void updateMap(Mat area_, StateTime stateTime, float timeDelta) {
    Mat area = area_.clone();
    // get lidar polygon and transform to pixel values
    Se2Bijection gokart2world = new Se2Bijection(stateTime.state());
    world2pixelLayer.pushMatrix(gokart2world.forward_se2());
    Tensor poly = lidar.getPolygon(stateTime);
    //  ---
    // transform lidar polygon to pixel values
    Tensor tens = Tensor.of(poly.stream().map(world2pixelLayer::toVector));
    world2pixelLayer.popMatrix();
    Point polygonPoint = CvHelper.tensorToPoint(tens); // reformat polygon to point
    // ---
    // fill lidar polygon and subtract it from shadow region
    Mat lidarMat = new Mat(initArea.size(), area.type(), opencv_core.Scalar.BLACK);
    opencv_imgproc.fillPoly(lidarMat, polygonPoint, new int[] { tens.length() }, 1, opencv_core.Scalar.WHITE);
    opencv_core.subtract(area, lidarMat, area);
    //  ---
    // dilate and intersect
    int it = radius2it(ellipseKernel, timeDelta * vMax);
    opencv_imgproc.dilate(area, area, ellipseKernel, new Point(-1, -1), it, opencv_core.BORDER_CONSTANT, null);
    opencv_core.bitwise_and(initArea, area, area);
    area.copyTo(area_);
  }

  // @Override
  public void updateMapGpu(GpuMat area, StateTime stateTime, float timeDelta) {
    // get lidar polygon and transform to pixel values
    Se2Bijection gokart2world = new Se2Bijection(stateTime.state());
    world2pixelLayer.pushMatrix(gokart2world.forward_se2());
    Tensor poly = lidar.getPolygon(stateTime);
    //  ---
    // transform lidar polygon to pixel values
    Tensor tens = Tensor.of(poly.stream().map(world2pixelLayer::toVector));
    world2pixelLayer.popMatrix();
    Point polygonPoint = CvHelper.tensorToPoint(tens); // reformat polygon to point
    // ---
    // fill lidar polygon and subtract it from shadow region
    Mat lidarMat = new Mat(initArea.size(), area.type(), opencv_core.Scalar.BLACK);
    opencv_imgproc.fillPoly(lidarMat, polygonPoint, new int[] { tens.length() }, 1, opencv_core.Scalar.WHITE);
    lidarMatGpu.upload(lidarMat);
    int it = radius2it(ellipseKernel, timeDelta * vMax);
    opencv_cudaarithm.subtract(area, lidarMatGpu, area);
    //  ---
    // dilate and intersect
    Filter filter = opencv_cudafilters.createMorphologyFilter(opencv_imgproc.MORPH_DILATE, area.type(), ellipseKernel, new Point(-1, -1), it);
    filter.apply(area, area);
    opencv_cudaarithm.bitwise_and(initAreaGpu, area, area);
  }

  public final Mat getCurrentMap() {
    return shadowArea.clone();
  }

  @Override
  public final Mat getInitMap() {
    return initArea.clone();
  }

  public void setColor(Color color) {
    COLOR_SHADOW_FILL = color;
  }

  private final int radius2it(final Mat spericalKernel, float radius) {
    float pixelRadius = spericalKernel.arrayWidth() / 2.0f;
    float worldRadius = pixelRadius * pixelDim.number().floatValue();
    return (int) Math.ceil(radius / worldRadius);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // shadowAreaGpu.download(shadowArea);
    //
    final Tensor matrix = geometricLayer.getMatrix();
    AffineTransform transform = AffineTransforms.toAffineTransform(matrix.dot(pixel2world));
    Mat plotArea = shadowArea.clone();
    // setup colorspace
    opencv_imgproc.cvtColor(plotArea, plotArea, opencv_imgproc.CV_GRAY2RGBA);
    Mat color = new Mat(4, 1, opencv_core.CV_8UC4);
    byte[] a = { (byte) COLOR_SHADOW_FILL.getAlpha(), //
        (byte) COLOR_SHADOW_FILL.getGreen(), //
        (byte) COLOR_SHADOW_FILL.getRed(), //
        (byte) COLOR_SHADOW_FILL.getBlue() };
    color.data().put(a);
    plotArea.setTo(color, plotArea);
    //  convert to bufferedimage
    BufferedImage img = new BufferedImage(plotArea.arrayWidth(), plotArea.arrayHeight(), BufferedImage.TYPE_4BYTE_ABGR);
    byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
    plotArea.data().get(data);
    graphics.drawImage(img, transform, null);
  }
}
