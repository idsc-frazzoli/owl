// code by ynager
package ch.ethz.idsc.owl.mapping;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.GpuMat;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_cudaarithm;
import org.bytedeco.javacpp.opencv_cudafilters;
import org.bytedeco.javacpp.opencv_cudafilters.Filter;
import org.bytedeco.javacpp.opencv_imgproc;

import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.data.img.CvHelper;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

public class ShadowMapSpherical extends ShadowMapCV implements RenderInterface {
  private Color COLOR_SHADOW_FILL;
  // ---
  private final LidarEmulator lidar;
  private final Mat initArea;
  private final Mat shadowArea;
  private final float vMax;
  private final Mat sphericalKernel = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_ELLIPSE, //
      new Size(7, 7));
  private final float kernelWorldRadius;
  private final float rMin;
  // ---
  Mat negSrc = new Mat();
  // ---
  private GpuMat initAreaGpu;
  private GpuMat shadowAreaGpu;
  private GpuMat lidarMatGpu;
  private boolean useGPU = false;

  public ShadowMapSpherical(LidarEmulator lidar, ImageRegion imageRegion, float vMax, float rMin) {
    super(imageRegion);
    this.lidar = lidar;
    this.vMax = vMax;
    this.rMin = rMin;
    this.kernelWorldRadius = sphericalKernel.arrayWidth() / 2.0f * pixelDim.number().floatValue();
    Mat area = CvHelper.bufferedImageToMat(bufferedImage);
    opencv_imgproc.threshold(area, area, 254, 255, opencv_imgproc.THRESH_BINARY_INV); // TODO magic consts
    //
    Mat obstacleArea = area.clone();
    initArea = new Mat(obstacleArea.size(), obstacleArea.type(), org.bytedeco.javacpp.opencv_core.Scalar.WHITE);
    opencv_imgproc.erode(initArea, initArea, sphericalKernel, new Point(-1, -1), 1, opencv_core.BORDER_CONSTANT, null);
    // opencv_imgproc.dilate(obstacleArea, obstacleArea, sphericalKernel, new Point(-1, -1), radius2it(rMin), opencv_core.BORDER_CONSTANT, null);
    Mat radPx = new Mat(Scalar.all((rMin) / pixelDim.number().floatValue()));
    opencv_core.bitwise_not(obstacleArea, negSrc);
    opencv_imgproc.distanceTransform(negSrc, obstacleArea, opencv_imgproc.CV_DIST_L2, opencv_imgproc.CV_DIST_MASK_PRECISE);
    opencv_core.compare(obstacleArea, radPx, obstacleArea, opencv_core.CMP_LE);
    opencv_core.subtract(initArea, obstacleArea, initArea);
    this.shadowArea = initArea.clone();
    setColor(new Color(255, 50, 74));
    //
    if (useGPU) {
      initAreaGpu = new GpuMat(initArea.size(), initArea.type());
      shadowAreaGpu = new GpuMat(shadowArea.size(), shadowArea.type());
      lidarMatGpu = new GpuMat(initArea.size(), initArea.type());
      shadowAreaGpu.upload(shadowArea);
      initAreaGpu.upload(initArea);
    }
  }

  @Override // from ShadowMap
  public void updateMap(StateTime stateTime, float timeDelta) {
    if (useGPU)
      updateMapGpu(shadowAreaGpu, stateTime, timeDelta);
    else
      updateMap(shadowArea, stateTime, timeDelta);
  }

  @Override // from ShadowMap
  public final Mat getInitMap() {
    return initArea.clone();
  }

  @Override // from ShadowMap
  public float getMinTimeDelta() {
    if (useGPU)
      return sphericalKernel.arrayWidth() * pixelDim.number().floatValue() / (2.0f * vMax);
    return 3 * pixelDim.number().floatValue() / vMax;
  }

  @Override
  public void updateMap(Mat area_, StateTime stateTime, float timeDelta) {
    // Stopwatch s = Stopwatch.started();
    GeometricLayer world2pixelLayer = GeometricLayer.of(world2pixel);
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
    // int it = radius2it(timeDelta * vMax);
    // opencv_imgproc.dilate(area, area, sphericalKernel, new Point(-1, -1), it, opencv_core.BORDER_CONSTANT, null);
    Mat rad = new Mat(Scalar.all((timeDelta * vMax) / pixelDim.number().floatValue()));
    opencv_core.bitwise_not(area, negSrc);
    opencv_imgproc.distanceTransform(negSrc, area, opencv_imgproc.CV_DIST_L2, opencv_imgproc.CV_DIST_MASK_PRECISE);
    opencv_core.compare(area, rad, area, opencv_core.CMP_LE);
    // ---
    opencv_core.bitwise_and(initArea, area, area);
    area.copyTo(area_);
    // System.out.println(s.display_nanoSeconds());
  }

  public void updateMapGpu(GpuMat area_, StateTime stateTime, float timeDelta) {
    GpuMat area = area_.clone();
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
    int it = radius2it(timeDelta * vMax);
    opencv_cudaarithm.subtract(area, lidarMatGpu, area);
    //  ---
    // dilate and intersect
    Filter filter = opencv_cudafilters.createMorphologyFilter(opencv_imgproc.MORPH_DILATE, area.type(), sphericalKernel, new Point(-1, -1), it);
    filter.apply(area, area);
    opencv_cudaarithm.bitwise_and(initAreaGpu, area, area);
    area.copyTo(area_);
  }

  @Override
  public final Mat getShape(Mat map, float carRad) {
    Mat shape = map.clone();
    Mat radPx = new Mat(Scalar.all((rMin + carRad) / pixelDim.number().floatValue()));
    Mat negSrc = new Mat(shape.size(), shape.type());
    opencv_core.bitwise_not(shape, negSrc);
    opencv_imgproc.distanceTransform(negSrc, shape, opencv_imgproc.CV_DIST_L2, opencv_imgproc.CV_DIST_MASK_PRECISE);
    opencv_core.compare(shape, radPx, shape, opencv_core.CMP_LE);
    return shape;
  }

  public final Mat getCurrentMap() {
    return shadowArea.clone();
  }

  public void setColor(Color color) {
    COLOR_SHADOW_FILL = color;
  }

  private final int radius2it(float radius) {
    return (int) Math.ceil(radius / kernelWorldRadius);
  }

  public void useGPU() {
    this.useGPU = true;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (useGPU)
      shadowAreaGpu.download(shadowArea);
    //
    final Tensor matrix = geometricLayer.getMatrix();
    AffineTransform transform = AffineTransforms.toAffineTransform(matrix.dot(pixel2world));
    Mat plotArea = shadowArea.clone();
    // setup colorspace
    opencv_imgproc.cvtColor(plotArea, plotArea, opencv_imgproc.CV_GRAY2RGBA);
    Mat color = new Mat(4, 1, opencv_core.CV_8UC4);
    byte[] a = StaticHelper.toAGRB(COLOR_SHADOW_FILL);
    color.data().put(a);
    plotArea.setTo(color, plotArea);
    //  convert to bufferedimage
    BufferedImage img = new BufferedImage(plotArea.arrayWidth(), plotArea.arrayHeight(), BufferedImage.TYPE_4BYTE_ABGR);
    byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
    plotArea.data().get(data);
    graphics.drawImage(img, transform, null);
  }
}
