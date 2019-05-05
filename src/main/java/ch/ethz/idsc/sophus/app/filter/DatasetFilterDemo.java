// code by jph, ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.List;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplayDemo;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;

/* package */ abstract class DatasetFilterDemo extends GeodesicDisplayDemo {
  static final Color COLOR_CURVE = new Color(255, 128, 128, 255);
  static final Color COLOR_SHAPE = new Color(160, 160, 160, 192);
  static final GridRender GRID_RENDER = new GridRender(Subdivide.of(0, 100, 10));
  // ---
  final JToggleButton jToggleWait = new JToggleButton("wait");
  final JToggleButton jToggleDiff = new JToggleButton("diff");
  final JToggleButton jToggleData = new JToggleButton("data");
  final JToggleButton jToggleConv = new JToggleButton("conv");
  // ---
  final PathRender pathRenderCurve = new PathRender(COLOR_CURVE);
  final PathRender pathRenderShape = new PathRender(COLOR_SHAPE);
  protected final JToggleButton jToggleSymi = new JToggleButton("graph");

  public DatasetFilterDemo(List<GeodesicDisplay> list) {
    super(list);
  }

  @Override
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleWait.isSelected())
      return;
    GRID_RENDER.render(geometricLayer, graphics);
    Tensor control = control();
    GraphicsUtil.setQualityHigh(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    final Tensor shape = geodesicDisplay.shape().multiply(markerScale());
    if (jToggleData.isSelected()) {
      pathRenderCurve.setCurve(control, false).render(geometricLayer, graphics);
      for (Tensor point : control) {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(shape);
        path2d.closePath();
        graphics.setColor(new Color(255, 128, 128, 64));
        graphics.fill(path2d);
        graphics.setColor(COLOR_CURVE);
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
    Tensor refined = protected_render(geometricLayer, graphics);
    graphics.setStroke(new BasicStroke(1f));
    if (jToggleConv.isSelected()) {
      pathRenderShape.setCurve(refined, false).render(geometricLayer, graphics);
      for (Tensor point : refined) {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(shape);
        path2d.closePath();
        graphics.setColor(COLOR_SHAPE);
        graphics.fill(path2d);
        graphics.setColor(Color.BLACK);
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
    if (jToggleDiff.isSelected())
      differences_render(graphics, geodesicDisplay(), refined);
  }

  public Scalar markerScale() {
    return RealScalar.of(.3);
  }

  protected abstract Tensor control();

  protected abstract Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics);

  protected abstract void differences_render(Graphics2D graphics, GeodesicDisplay geodesicDisplay, Tensor refined);
}
