// code by ynager
package ch.ethz.idsc.owl.mapping;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.function.Supplier;

import javax.swing.WindowConstants;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.data.img.ImageAlpha;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

public class ShadowMapJavaCV implements RenderInterface {
  //
  private Color COLOR_SHADOW_FILL;
  private Color COLOR_SHADOW_DRAW;
  // ---
  private final LidarEmulator lidar;
  public final Supplier<StateTime> stateTimeSupplier;
  private final Mat initArea;
  private Mat shadowArea;
  private final float vMax;
  private final float rMin;
  DoubleScalar pixelDim;
  DoubleScalar pixelDimInv;
  private final GeometricLayer world2pixelLayer;
  
  Tensor world2pixel;
  Tensor pixel2world;
  Tensor scaling;

  public ShadowMapJavaCV(LidarEmulator lidar, ImageRegion imageRegion, Supplier<StateTime> stateTimeSupplier, float vMax, float rMin) {
    this.lidar = lidar;
    this.stateTimeSupplier = stateTimeSupplier;
    this.vMax = vMax;
    this.rMin = rMin;
    BufferedImage bufferedImage = RegionRenders.image(imageRegion.image());
    // TODO 244 and 5 magic const, redundant to values specified elsewhere
    Mat area = bufferedImageToMat(bufferedImage);
    opencv_imgproc.threshold(area, area, 254, 255, opencv_imgproc.THRESH_BINARY_INV);
    //
    // convert imageRegion into Area
    Tensor scale = imageRegion.scale(); // pixels per meter
    ch.ethz.idsc.tensor.Scalar pixelDim = scale.Get(0).reciprocal(); // meters per pixel
    scaling = DiagonalMatrix.of(pixelDim, pixelDim.negate(), RealScalar.ONE).unmodifiable();
    
    world2pixel = DiagonalMatrix.of(scale.Get(0), scale.Get(1).negate(), RealScalar.ONE);
    world2pixel.set(RealScalar.of(bufferedImage.getHeight()), 1, 2);
    //
    pixel2world = DiagonalMatrix.of( //
        scale.Get(0).reciprocal(), scale.Get(1).negate().reciprocal(), RealScalar.ONE);
    pixel2world.set(RealScalar.of(bufferedImage.getHeight()).multiply(scale.Get(1).reciprocal()), 1, 2);
    
    world2pixelLayer = GeometricLayer.of(world2pixel);
    
    // 
    Mat obstacleArea = new Mat();
    obstacleArea = area;
    //opencv_imgproc.resize(area, obstacleArea, new Size(area.arrayWidth()/2, area.arrayHeight()/2));
    //opencv_imgproc.warpAffine(area, obstacleArea, tmat, new Size(area.arrayWidth(), area.arrayHeight()));
    //display(obstacleArea, "obsArea");
    //obstacleArea = area;
    //
    //Rectangle2D rInit = new Rectangle2D.Double();
    //rInit.setRect(0, 0, obstacleArea.arrayWidth(), obstacleArea.arrayHeight());
    //initArea = new Area(rInit);
    Mat rMinEllipse = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_ELLIPSE, //
        new Size(5, 5));
    initArea = new Mat(obstacleArea.size(), obstacleArea.type(), Scalar.WHITE);
    opencv_imgproc.erode(initArea, initArea, rMinEllipse);
    opencv_imgproc.dilate(obstacleArea, obstacleArea, rMinEllipse);
    //initArea.subtract(obstacleArea);
    opencv_core.subtract(initArea, obstacleArea, initArea);
    //display(obstacleArea, "obstacleArea");
    this.shadowArea = initArea.clone();
    setColor(new Color(255, 50, 74));
  }

  public void updateMap(StateTime stateTime, float timeDelta) {
    updateMap(shadowArea, stateTime, timeDelta);
  }

  public void updateMap(Mat area, StateTime stateTime, float timeDelta) {
    Se2Bijection gokart2world = new Se2Bijection(stateTime.state());
    GeometricLayer geom = new GeometricLayer(world2pixel, Array.zeros(3));
    geom.pushMatrix(gokart2world.forward_se2());
    Path2D lidarPath2D = geom.toPath2D(lidar.getPolygon(stateTime));
    
    BufferedImage img = new BufferedImage(initArea.arrayWidth(), initArea.arrayHeight(), BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D graphics = ((Graphics2D) img.getGraphics());
    graphics.setColor(new Color(255, 255, 255));
    graphics.fill(lidarPath2D);
    Mat lidarMat = new Mat(initArea.size(), area.type());
    
    byte[] buffer = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
    lidarMat.data().put(buffer);
    
    Mat ellipse = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_ELLIPSE, //
        new Size(3, 3));

    // rmin
    opencv_imgproc.dilate(lidarMat, lidarMat, ellipse, new Point(-1, -1), 1, opencv_core.BORDER_CONSTANT, null);
    //area.subtract(lidarArea);
    opencv_core.subtract(area, lidarMat, area);
    // timeDelta * vMax
    opencv_imgproc.dilate(area, area, ellipse, new Point(-1, -1), 2, opencv_core.BORDER_CONSTANT, null);
    //area.intersect(initArea);
    opencv_core.bitwise_and(initArea, area, area);
    
  }

  public final Mat getCurrentMap() {
    return shadowArea.clone();
  }

  public final Mat getInitMap() {
    return initArea.clone();
  }

  public void setColor(Color color) {
    COLOR_SHADOW_FILL = new Color((color.getRGB() & 0xFFFFFF) | (16 << 24), true);
    COLOR_SHADOW_DRAW = new Color((color.getRGB() & 0xFFFFFF) | (64 << 24), true);
  }
  
  public static Mat bufferedImageToMat(BufferedImage bi) {
    Mat mat = new Mat(bi.getHeight(), bi.getWidth(), opencv_core.CV_8U);
    byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
    mat.data().put(data);
    return mat;
  }
  

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final Tensor matrix = geometricLayer.getMatrix();
    //AffineTransform transform = AffineTransforms.toAffineTransform(matrix.dot(tmatrix));
    AffineTransform transform = AffineTransforms.toAffineTransform(matrix.dot(pixel2world));
    
    Mat plotArea = shadowArea.clone();
    opencv_imgproc.cvtColor(plotArea,plotArea,opencv_imgproc.CV_GRAY2RGBA);
    
    // convert to bufferedimage
    BufferedImage img = new BufferedImage(plotArea.arrayWidth(), plotArea.arrayHeight(), BufferedImage.TYPE_4BYTE_ABGR);
    byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
    plotArea.data().get(data);
    
    graphics.setColor(COLOR_SHADOW_FILL);
    //img = ImageAlpha.scale(img, 0.2f);
    graphics.drawImage(img, transform, null);
    //graphics.setColor(COLOR_SHADOW_DRAW);
    //graphics.draw(plotArea);
  }
  
  
  static void display(Mat image, String caption) {
    // Create image window named "My Image".
    final CanvasFrame canvas = new CanvasFrame(caption, 1.0);
    // Request closing of the application when the image window is closed.
    canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    // Convert from OpenCV Mat to Java Buffered image for display
    final OpenCVFrameConverter converter = new OpenCVFrameConverter.ToMat();
    // Show image on window.
    canvas.showImage(converter.convert(image));
  }
}
