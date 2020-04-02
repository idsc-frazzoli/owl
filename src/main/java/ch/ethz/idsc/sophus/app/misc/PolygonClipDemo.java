// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.sophus.ply.CogPoints;
import ch.ethz.idsc.sophus.ply.PolygonClip;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ class PolygonClipDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.strict();
  private static final Tensor CIRCLE = CirclePoints.of(7).multiply(RealScalar.of(4));
  private static final TensorUnaryOperator POLYGON_CLIP = PolygonClip.of(CIRCLE);

  public PolygonClipDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    setControlPointsSe2(Tensor.of(CogPoints.of(4, RealScalar.of(5), RealScalar.of(-2)).stream().map(row -> row.append(RealScalar.ZERO))));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    new PathRender(COLOR_DATA_INDEXED.getColor(3), 1.5f).setCurve(CIRCLE, true).render(geometricLayer, graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor control = getGeodesicControlPoints();
    new PathRender(COLOR_DATA_INDEXED.getColor(0), 1.5f).setCurve(control, true).render(geometricLayer, graphics);
    Tensor result = POLYGON_CLIP.apply(control);
    graphics.setColor(new Color(128, 255, 128, 128));
    graphics.fill(geometricLayer.toPath2D(result));
    new PathRender(COLOR_DATA_INDEXED.getColor(1), 2.5f).setCurve(result, true).render(geometricLayer, graphics);
    GraphicsUtil.setQualityDefault(graphics);
  }

  public static void main(String[] args) {
    new PolygonClipDemo().setVisible(1000, 600);
  }
}
