// code by jph
package ch.ethz.idsc.sophus.app.fun;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ class LaserTagDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.strict().deriveWithAlpha(128);
  // ---
  private static final String TEXT = "WILLKOMMEN IN"; // "NIEDERSACHSEN";
  private final PathRender pathRenderHull = new PathRender(COLOR_DATA_INDEXED.getColor(1), 1.5f);
  private final JToggleButton jToggleButton = new JToggleButton("show");

  public LaserTagDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    // ---
    timerFrame.geometricComponent.addRenderInterface(pathRenderHull);
    jToggleButton.setSelected(true);
    timerFrame.jToolBar.add(jToggleButton);
    // ---
    Distribution distribution = UniformDistribution.of(-4, 4);
    setControlPointsSe2(RandomVariate.of(distribution, TEXT.length() + 2, 3));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor control = getGeodesicControlPoints();
    graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
    if (jToggleButton.isSelected()) {
      graphics.draw(geometricLayer.toPath2D(control));
      renderControlPoints(geometricLayer, graphics);
    } else {
      graphics.draw(geometricLayer.toPath2D(control.extract(0, 3)));
    }
    graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
    for (int index = 1; index < control.length() - 1; ++index) {
      Tensor p = control.get(index - 1);
      Tensor q = control.get(index);
      Tensor r = control.get(index + 1);
      Tensor d1 = Vector2Norm.NORMALIZE.apply(p.subtract(q));
      Tensor d2 = Vector2Norm.NORMALIZE.apply(r.subtract(q));
      Tensor o1 = Vector2Norm.NORMALIZE.apply(d1.add(d2));
      Tensor o2 = Cross.of(o1);
      geometricLayer.pushMatrix(Se2Matrix.translation(q));
      Tensor polygon = Tensors.of(o2, o1.negate(), o2.negate());
      Path2D path2d = geometricLayer.toPath2D(polygon);
      graphics.setColor(COLOR_DATA_INDEXED.getColor(1));
      graphics.draw(path2d);
      graphics.setColor(COLOR_DATA_INDEXED.getColor(1));
      graphics.fill(path2d);
      Point2D point2d = geometricLayer.toPoint2D(o1.multiply(RealScalar.of(-0.5)));
      graphics.setColor(Color.DARK_GRAY);
      graphics.drawString("" + TEXT.charAt(index - 1), (int) point2d.getX() - 8, (int) point2d.getY() + 10);
      geometricLayer.popMatrix();
    }
  }

  public static void main(String[] args) {
    new LaserTagDemo().setVisible(1000, 600);
  }
}
