// code by ynager
package ch.ethz.idsc.owl.mapping;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.data.img.ImageArea;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

public class ShadowMap implements RenderInterface {
  //
  private Color COLOR_SHADOW_FILL;
  private Color COLOR_SHADOW_DRAW;
  // ---
  private final LidarEmulator lidar;
  public final Supplier<StateTime> stateTimeSupplier;
  private boolean isPaused = false;
  private final Area initArea;
  private Area shadowArea;
  private Timer increaserTimer;
  private final float vMax;
  private final float rMin;
  public ShadowMap(LidarEmulator lidar, ImageRegion imageRegion, Supplier<StateTime> stateTimeSupplier, float vMax, float rMin) {
    this.lidar = lidar;
    this.stateTimeSupplier = stateTimeSupplier;
    this.vMax = vMax;
    this.rMin = rMin;
    BufferedImage bufferedImage = RegionRenders.image(imageRegion.image());
    // TODO 244 and 5 magic const, redundant to values specified elsewhere
    Area area = ImageArea.fromImage(bufferedImage, new Color(244, 244, 244), 5);
    //
    // convert imageRegion into Area
    Tensor scale = imageRegion.scale();
    Tensor invsc = DiagonalMatrix.of( //
        scale.Get(0).reciprocal(), scale.Get(1).negate().reciprocal(), RealScalar.ONE);
    Tensor translate = IdentityMatrix.of(3);
    translate.set(RealScalar.of(-bufferedImage.getHeight()), 1, 2);
    Tensor tmatrix = invsc.dot(translate);
    Area obstacleArea = area.createTransformedArea(AffineTransforms.toAffineTransform(tmatrix));
    Rectangle2D rInit = new Rectangle2D.Double();
    rInit.setFrame(obstacleArea.getBounds());
    initArea = new Area(rInit);
    erode(initArea, rMin);
    dilate(obstacleArea, rMin);
    initArea.subtract(obstacleArea);
    this.shadowArea = new Area(initArea);
    setColor(new Color(255, 50, 74));
  }

  public void updateMap(Area area, StateTime stateTime, float timeDelta) {
    Se2Bijection se2Bijection = new Se2Bijection(stateTime.state());
    GeometricLayer geom = new GeometricLayer(se2Bijection.forward_se2(), Array.zeros(3));
    Path2D lidarPath2D = geom.toPath2D(lidar.getPolygon(stateTime));
    Area lidarArea = new Area(lidarPath2D);
    dilate(lidarArea, rMin);
    area.subtract(lidarArea);
    dilate(area, timeDelta * vMax);
    area.intersect(initArea);
  }

  /* TODO: Stoke should have cap and join as BasicStroke.CAP_ROUND, this
   * / however reduces performance */
  protected void dilate(Area area, float radius) {
    Stroke stroke = new BasicStroke(radius * 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
    Shape strokeShape = stroke.createStrokedShape(area);
    Area strokeArea = new Area(strokeShape);
    area.add(strokeArea);
  }

  protected void erode(Area area, float radius) {
    Stroke stroke = new BasicStroke(radius * 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
    Shape strokeShape = stroke.createStrokedShape(area);
    Area strokeArea = new Area(strokeShape);
    area.subtract(strokeArea);
  }

  public final void startNonBlocking(int updateRate) {
    TimerTask mapUpdate = new TimerTask() {
      @Override
      public void run() {
        if (!isPaused)
          updateMap(shadowArea, stateTimeSupplier.get(), 1.0f / updateRate);
      }
    };
    increaserTimer = new Timer("MapUpdateTimer");
    increaserTimer.scheduleAtFixedRate(mapUpdate, 10, 1000 / updateRate);
  }

  public final void flagShutdown() {
    increaserTimer.cancel();
  }

  public final void pause() {
    isPaused = true;
  }

  public final void resume() {
    isPaused = false;
  }

  public final Area getCurrentMap() {
    return shadowArea;
  }
  
  public final Area getInitMap() {
    return initArea;
  }

  public void setColor(Color color) {
    COLOR_SHADOW_FILL = new Color((color.getRGB() & 0xFFFFFF) | (16 << 24), true);
    COLOR_SHADOW_DRAW = new Color((color.getRGB() & 0xFFFFFF) | (64 << 24), true);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final Tensor matrix = geometricLayer.getMatrix();
    Area plotArea = new Area(shadowArea.createTransformedArea(AffineTransforms.toAffineTransform(matrix)));
    graphics.setColor(COLOR_SHADOW_FILL);
    graphics.fill(plotArea);
    graphics.setColor(COLOR_SHADOW_DRAW);
    graphics.draw(plotArea);
  }
}
