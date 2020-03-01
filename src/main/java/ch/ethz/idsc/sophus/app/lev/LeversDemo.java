// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.util.List;

import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.PointsRender;

/* package */ abstract class LeversDemo extends ControlPointsDemo {
  static final PointsRender POINTS_RENDER_0 = //
      new PointsRender(new Color(255, 128, 128, 64), new Color(255, 128, 128, 255));
  static final PointsRender ORIGIN_RENDER_0 = //
      new PointsRender(new Color(64, 128, 64, 128), new Color(64, 128, 64, 255));
  static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);

  public LeversDemo(List<GeodesicDisplay> list) {
    super(true, list);
    setMidpointIndicated(false);
  }
}
