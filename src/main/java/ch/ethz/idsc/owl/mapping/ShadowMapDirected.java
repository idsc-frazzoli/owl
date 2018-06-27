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

import javax.swing.WindowConstants;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Point2f;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.indexer.UByteBufferIndexer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
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
  //
  private Color COLOR_SHADOW_FILL;
  // ---
  private final LidarEmulator lidar;
  private Mat initArea;
  private Mat shadowArea;
  private final float vMax;
  Scalar pixelDim;
  Scalar pixelDimInv;
  private final GeometricLayer world2pixelLayer;
  private final Mat ellipseKernel = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_ELLIPSE, //
      new Size(5, 5));
  Tensor world2pixel;
  Tensor pixel2world;
  Tensor scaling;
  private final List<Mat> laneMasks = new ArrayList<>();
  private final List<Mat> updateKernels = new ArrayList<>();
  private final List<Mat> carKernels = new ArrayList<>();
  private final static int NSEGS = 40;
  private final Mat obsDil;

  public ShadowMapDirected(LidarEmulator lidar, ImageRegion imageRegion, float vMax) {
    this.lidar = lidar;
    this.vMax = vMax;
    // setup
    // TODO pass obstacles and lanes as arguments
    URL carLanes = getClass().getResource("/map/scenarios/S1_car_lanes.BMP");
    URL carObs = getClass().getResource("/map/scenarios/S1_car_obs.BMP");
    URL kernel = getClass().getResource("/cv/kernels/kernel6.bmp");
    float pixelPerSeg = 253.0f / (NSEGS);
    int[] limits = IntStream.rangeClosed(0, NSEGS).map(i -> (int) (i * pixelPerSeg)).toArray();
    Mat img = opencv_imgcodecs.imread(carLanes.getPath(), opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
    Mat kernOrig = opencv_imgcodecs.imread(kernel.getPath(), opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
    Mat carKernel = Mat.ones(new Size(5, 5), kernOrig.type()).asMat();
    byte[] a = { 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0 };
    carKernel.data().put(a);
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
    Mat obs = opencv_imgcodecs.imread(carObs.getPath(), opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
    Mat obsLane = new Mat(obs.size(), obs.type());
    opencv_imgproc.threshold(img, obsLane, 0, 255, opencv_imgproc.THRESH_BINARY_INV);
    opencv_imgproc.dilate(obsLane, obsLane, ellipseKernel);
    Mat obsTot = new Mat(obs.size(), obs.type());
    opencv_core.add(obs, obsLane, obsTot);
    obsDil = Mat.zeros(obs.size(), obs.type()).asMat();
    opencv_imgproc.dilate(obsLane, obsDil, ellipseKernel, new Point(2, 2), 9, opencv_core.BORDER_CONSTANT, null); // TODO magic
    List<Mat> obsList = new ArrayList<>();
    // TODO use spherical for lanes, carkernel for other obstacles
    IntStream.range(0, NSEGS).parallel().forEach( //
        seg -> dilateSegment(seg, obs, carKernels, new Point(2, 2), laneMasks, obsList, 15)); // TODO magic iteration number
    obsList.parallelStream().forEach((upimg) -> opencv_core.add(obsDil, upimg, obsDil));
    //
    Tensor scale = imageRegion.scale(); // pixels per meter
    pixelDim = scale.Get(0).reciprocal(); // meters per pixel
    scaling = DiagonalMatrix.of(pixelDim, pixelDim.negate(), RealScalar.ONE).unmodifiable();
    world2pixel = DiagonalMatrix.of(scale.Get(0), scale.Get(1).negate(), RealScalar.ONE);
    world2pixel.set(RealScalar.of(img.arrayHeight()), 1, 2);
    //
    pixel2world = DiagonalMatrix.of( //
        scale.Get(0).reciprocal(), scale.Get(1).negate().reciprocal(), RealScalar.ONE);
    pixel2world.set(RealScalar.of(img.arrayHeight()).multiply(scale.Get(1).reciprocal()), 1, 2);
    world2pixelLayer = GeometricLayer.of(world2pixel);
    //
    initArea = new Mat(img.size(), img.type(), opencv_core.Scalar.WHITE);
    opencv_imgproc.erode(initArea, initArea, ellipseKernel, new Point(-1, -1), 1, opencv_core.BORDER_CONSTANT, null);
    opencv_core.subtract(initArea, obsDil, initArea);
    this.shadowArea = initArea.clone();
    setColor(new Color(255, 50, 74));
  }

  private static void dilateSegment(int s, Mat region, List<Mat> kernels, Point kernCenter, List<Mat> masks, List<Mat> list, int iterations) {
    Mat updatedSegment = new Mat(region.size(), region.type());
    opencv_core.bitwise_and(region, masks.get(s), updatedSegment);
    opencv_imgproc.dilate(updatedSegment, updatedSegment, kernels.get(s), kernCenter, iterations, opencv_core.BORDER_CONSTANT, null);
    synchronized (list) {
      list.add(updatedSegment);
    }
  }

  public void updateMap(StateTime stateTime, float timeDelta) {
    updateMap(shadowArea, stateTime, timeDelta);
  }

  public Point state2pixel(Tensor state) {
    GeometricLayer layer = GeometricLayer.of(world2pixel);
    Point2D point2D = layer.toPoint2D(state);
    return new Point( //
        (int) point2D.getX(), //
        (int) point2D.getY());
  }

  public void updateMap(Mat area_, StateTime stateTime, float timeDelta) {
    synchronized (world2pixelLayer) {
      // get lidar polygon and transform to pixel values
      Mat area = area_.clone();
      List<Mat> updatedMatList = new ArrayList<>();
      Se2Bijection gokart2world = new Se2Bijection(stateTime.state());
      world2pixelLayer.pushMatrix(gokart2world.forward_se2());
      Tensor poly = lidar.getPolygon(stateTime);
      //  ---
      // transform lidar polygon to pixel values
      Tensor tens = Tensor.of(poly.stream().map(world2pixelLayer::toVector));
      world2pixelLayer.popMatrix();
      // put array into Point
      int[] intArr = new int[tens.length() * 2];
      for (int i = 0; i < tens.length(); i++) {
        intArr[i * 2] = tens.get(i).Get(0).number().intValue();
        intArr[i * 2 + 1] = tens.get(i).Get(1).number().intValue();
      }
      Point polygonPoint = new opencv_core.Point(intArr.length);
      polygonPoint.put(intArr, 0, intArr.length);
      // ---
      // fill lidar polygon and subtract it from shadow region
      Mat lidarMat = new Mat(initArea.size(), area.type(), opencv_core.Scalar.BLACK);
      opencv_imgproc.fillPoly(lidarMat, polygonPoint, new int[] { intArr.length / 2 }, 1, opencv_core.Scalar.WHITE);
      opencv_core.subtract(area, lidarMat, area);
      // expand shadow region according to lane direction
      // TODO this is a bottleneck! takes ~150ms
      // !!
      int it = radius2it(updateKernels.get(0), timeDelta * vMax); // TODO check if correct
      IntStream.range(0, NSEGS).parallel().forEach(s -> dilateSegment(s, area, updateKernels, new Point(-1, -1), laneMasks, updatedMatList, it));
      // Mat.zeros(area.size(), area.type()).asMat().assignTo(region);
      updatedMatList.stream().forEach(upimg -> opencv_core.add(area, upimg, area));
      opencv_core.subtract(area, obsDil, area);
      opencv_core.bitwise_and(initArea, area, area);
      // ---
      area.copyTo(area_);
    }
  }

  public final boolean isMember(Tensor state) {
    UByteBufferIndexer sI = shadowArea.createIndexer();
    return sI.get(0, 0) != 0;
  }

  public final Mat getCurrentMap() {
    return shadowArea.clone();
  }

  public final Mat getInitMap() {
    return initArea.clone();
  }

  public void setColor(Color color) {
    COLOR_SHADOW_FILL = color;
  }

  public static Mat bufferedImageToMat(BufferedImage bi) {
    Mat mat = new Mat(bi.getHeight(), bi.getWidth(), opencv_core.CV_8U);
    byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
    mat.data().put(data);
    return mat;
  }

  private final int radius2it(final Mat spericalKernel, float radius) {
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

  static void display(Mat image, String caption) {
    final CanvasFrame canvas = new CanvasFrame(caption, 1.0);
    canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    final OpenCVFrameConverter converter = new OpenCVFrameConverter.ToMat();
    canvas.showImage(converter.convert(image));
  }
}
