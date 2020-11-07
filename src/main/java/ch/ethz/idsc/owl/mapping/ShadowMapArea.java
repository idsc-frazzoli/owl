// code by ynager
package ch.ethz.idsc.owl.mapping;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.data.img.ImageArea;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.hs.r2.Se2Bijection;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

/** implementation uses only java default function and does not depend on 3rd party libraries */
public class ShadowMapArea implements RenderInterface {
  private final LidarEmulator lidar;
  private final Area initArea;
  private final Area shadowArea;
  private final float vMax;
  private final float rMin;
  //
  private Color colorShadowFill;
  private Color colorShadowDraw;

  /** @param lidar
   * @param imageRegion documentation!
   * @param vMax
   * @param rMin */
  public ShadowMapArea(LidarEmulator lidar, ImageRegion imageRegion, float vMax, float rMin) {
    this.lidar = lidar;
    this.vMax = vMax;
    this.rMin = rMin;
    Tensor image = imageRegion.image();
    Area area = ImageArea.fromTensor(image);
    //
    // convert imageRegion into Area
    Tensor scale = imageRegion.scale();
    Tensor invsc = Tensors.of( //
        scale.Get(0).reciprocal(), scale.Get(1).negate().reciprocal(), RealScalar.ONE);
    Tensor translate = IdentityMatrix.of(3);
    translate.set(RealScalar.of(-image.length()), 1, 2);
    Tensor tmatrix = invsc.pmul(translate);
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

  public void updateMap(StateTime stateTime, float timeDelta) {
    updateMap(shadowArea, stateTime, timeDelta);
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

  protected void dilate(Area area, float radius) {
    StaticHelper.makeStroke(area, radius, Area::add);
  }

  protected void erode(Area area, float radius) {
    StaticHelper.makeStroke(area, radius, Area::subtract);
  }

  public final Area getCurrentMap() {
    return new Area(shadowArea);
  }

  public final Area getInitMap() {
    return (Area) initArea.clone();
  }

  public void setColor(Color color) {
    colorShadowFill = new Color((color.getRGB() & 0xFFFFFF) | (16 << 24), true);
    colorShadowDraw = new Color((color.getRGB() & 0xFFFFFF) | (64 << 24), true);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final Tensor matrix = geometricLayer.getMatrix();
    Area plotArea = new Area(shadowArea.createTransformedArea(AffineTransforms.toAffineTransform(matrix)));
    graphics.setColor(colorShadowFill);
    graphics.fill(plotArea);
    graphics.setColor(colorShadowDraw);
    graphics.draw(plotArea);
  }
}
