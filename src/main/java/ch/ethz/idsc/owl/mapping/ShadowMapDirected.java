// code by ynager
package ch.ethz.idsc.owl.mapping;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Point2f;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_imgproc;

import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
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

public class ShadowMapDirected implements ShadowMap, RenderInterface {
  private final static int NSEGS = 40;
  private final static float CAR_WIDTH = 0.2f;
  // ---
  private final LidarEmulator lidar;
  private final Mat initArea;
  private final Mat shadowArea;
  private final Mat obsDilArea;
  private final Mat ellipseKernel = //
      opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_ELLIPSE, new Size(5, 5));
  private final float vMax;
  private final Scalar pixelDim;
  private final GeometricLayer world2pixelLayer;
  private final Tensor world2pixel;
  private final Tensor pixel2world;
  private final List<Mat> laneMasks = new ArrayList<>();
  private final List<Mat> updateKernels = new ArrayList<>();
  private final List<Mat> carKernels = new ArrayList<>();
  // ---
  private Color COLOR_SHADOW_FILL;

  public ShadowMapDirected(LidarEmulator lidar, ImageRegion imageRegion, float vMax) {
    this.lidar = lidar;
    this.vMax = vMax;
    // setup
    // TODO pass obstacles and lanes as arguments
    URL carLanesURL = getClass().getResource("/map/scenarios/s1/car_lanes.png");
    URL carObsURL = getClass().getResource("/map/scenarios/s1/car_obs.png");
    URL kernelURL = getClass().getResource("/cv/kernels/kernel6.bmp");
    float pixelPerSeg = 253.0f / (NSEGS);
    int[] limits = IntStream.rangeClosed(0, NSEGS).map(i -> (int) (i * pixelPerSeg)).toArray();
    Mat img = opencv_imgcodecs.imread(carLanesURL.getPath(), opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
    Mat kernOrig = opencv_imgcodecs.imread(kernelURL.getPath(), opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
    Mat carKernel = Mat.ones(new Size(5, 5), kernOrig.type()).asMat();
    byte[] a = { //
        0, 1, 1, 1, 0, //
        0, 1, 1, 1, 0, //
        0, 1, 1, 1, 0, //
        0, 1, 1, 1, 0, //
        0, 1, 1, 1, 0 };
    carKernel.data().put(a);
    //
    Tensor scale = imageRegion.scale(); // pixels per meter
    pixelDim = scale.Get(0).reciprocal(); // meters per pixel
    world2pixel = DiagonalMatrix.of(scale.Get(0), scale.Get(1).negate(), RealScalar.ONE);
    world2pixel.set(RealScalar.of(img.arrayHeight()), 1, 2);
    //
    // build rotated kernels
    for (int s = 0; s < NSEGS; s++) {
      Mat img0 = Mat.zeros(img.size(), img.type()).asMat();
      opencv_imgproc.threshold(img, img0, limits[s], 255, opencv_imgproc.THRESH_TOZERO);
      opencv_imgproc.threshold(img0, img0, limits[s + 1], 255, opencv_imgproc.THRESH_TOZERO_INV);
      opencv_imgproc.threshold(img0, img0, 0, 255, opencv_imgproc.THRESH_BINARY);
      laneMasks.add(img0.clone());
      // opencv_imgcodecs.imwrite("/home/ynager/Downloads/img/mask" + s + ".jpg", img0);
      // ---
      double angle = 360.0 / (NSEGS) * s;
      Mat kern = new Mat(kernOrig.size(), kernOrig.type());
      Mat carKern = new Mat(carKernel.size(), carKernel.type());
      Mat rotMatrix1 = opencv_imgproc.getRotationMatrix2D(new Point2f(7, 7), angle + 4, 1.0); // TOOO magic consts
      Mat rotMatrix2 = opencv_imgproc.getRotationMatrix2D(new Point2f(2, 2), angle + 4, 1.0);
      opencv_imgproc.warpAffine(kernOrig, kern, rotMatrix1, kernOrig.size());
      opencv_imgproc.warpAffine(carKernel, carKern, rotMatrix2, carKernel.size());
      updateKernels.add(kern);
      carKernels.add(carKern);
    }
    // build obstacle images
    Mat obsLane = new Mat(img.size(), img.type());
    opencv_imgproc.threshold(img, obsLane, 0, 255, opencv_imgproc.THRESH_BINARY_INV);
    opencv_imgproc.dilate(obsLane, obsLane, ellipseKernel);
    obsDilArea = Mat.zeros(img.size(), img.type()).asMat();
    // TODO
    opencv_imgproc.dilate(obsLane, obsDilArea, ellipseKernel, new Point(-1, -1), radius2it(ellipseKernel, CAR_WIDTH), opencv_core.BORDER_CONSTANT, null);
    // TODO use spherical for lanes, carkernel for other obstacles
    Mat carObs = opencv_imgcodecs.imread(carObsURL.getPath(), opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
    IntStream.range(0, NSEGS).parallel() //
        .mapToObj(seg -> StaticHelper.dilateSegment(seg, carObs, carKernels, new Point(2, 2), laneMasks, 15)) // TODO magic iteration number
        .forEach(upimg -> opencv_core.add(obsDilArea, upimg, obsDilArea));
    //
    pixel2world = DiagonalMatrix.of( //
        scale.Get(0).reciprocal(), scale.Get(1).negate().reciprocal(), RealScalar.ONE);
    pixel2world.set(RealScalar.of(img.arrayHeight()).divide(scale.Get(1)), 1, 2);
    world2pixelLayer = GeometricLayer.of(world2pixel);
    //
    initArea = new Mat(img.size(), img.type(), opencv_core.Scalar.WHITE);
    opencv_imgproc.erode(initArea, initArea, ellipseKernel, new Point(-1, -1), 1, opencv_core.BORDER_CONSTANT, null);
    opencv_core.subtract(initArea, obsDilArea, initArea);
    this.shadowArea = initArea.clone();
    setColor(new Color(255, 50, 74));
  }

  @Override // from ShadowMap
  public void updateMap(StateTime stateTime, float timeDelta) {
    updateMap(shadowArea, stateTime, timeDelta);
  }

  @Override
  public Point state2pixel(Tensor state) {
    GeometricLayer layer = GeometricLayer.of(world2pixel);
    Point2D point2D = layer.toPoint2D(state);
    return new Point( //
        (int) point2D.getX(), //
        (int) point2D.getY());
  }

  // if synchronization is needed simply declare "private synchronized void"
  private void updateMap(Mat area_, StateTime stateTime, float timeDelta) {
    // get lidar polygon and transform to pixel values
    Mat area = area_.clone();
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
    // expand shadow region according to lane direction
    // TODO this is a bottleneck! takes ~150ms
    // !!
    int it = radius2it(updateKernels.get(0), timeDelta * vMax); // TODO check if correct
    IntStream.range(0, NSEGS).parallel() //
        .mapToObj(s -> StaticHelper.dilateSegment(s, area, updateKernels, new Point(-1, -1), laneMasks, it)) //
        // Mat.zeros(area.size(), area.type()).asMat().assignTo(region);
        .forEach(upimg -> opencv_core.add(area, upimg, area));
    opencv_core.subtract(area, obsDilArea, area);
    opencv_core.bitwise_and(initArea, area, area);
    // ---
    area.copyTo(area_);
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

  private final int radius2it(Mat spericalKernel, float radius) {
    float pixelRadius = spericalKernel.arrayWidth() / 2.0f;
    float worldRadius = pixelRadius * pixelDim.number().floatValue();
    return (int) Math.ceil(radius / worldRadius);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
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
    BufferedImage bufferedImage = new BufferedImage(plotArea.arrayWidth(), plotArea.arrayHeight(), BufferedImage.TYPE_4BYTE_ABGR);
    byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
    plotArea.data().get(data);
    graphics.drawImage(bufferedImage, transform, null);
  }
}
