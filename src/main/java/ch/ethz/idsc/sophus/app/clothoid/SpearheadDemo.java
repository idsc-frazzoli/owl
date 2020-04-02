// code by jph
package ch.ethz.idsc.sophus.app.clothoid;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JTextField;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.ply.Spearhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

/* package */ class SpearheadDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(128);

  SpearheadDemo() {
    super(false, GeodesicDisplays.SE2_ONLY);
    // ---
    timerFrame.geometricComponent.addRenderInterface(AxesRender.INSTANCE);
    // ---
    JTextField jTextField = new JTextField(10);
    jTextField.setPreferredSize(new Dimension(100, 28));
    timerFrame.jToolBar.add(jTextField);
    // ---
    // {-0.842, -0.342, -0.524}
    // {-0.806, -0.250, -0.524}
    setControlPointsSe2(Tensors.fromString("{{-0.5, -0.5, 0.3}}"));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    Tensor control = getGeodesicControlPoints();
    renderControlPoints(geometricLayer, graphics);
    // Tensor p = control.get(0);
    // Tensor tip = Tensors.vector(1, 0, Math.PI);
    // Tensor q = p.pmul(Tensors.vector(1, -1, -1));
    // Tensor cp = Tensors.of(p, tip, q);
    // Tensor curve = Tensors.empty();
    Scalar width = RealScalar.of(geometricLayer.pixel2modelWidth(10));
    // for (int count = 0; count < 3; ++count) {
    // Tensor p1 = cp.get(count);
    // Tensor p2 = cp.get((count + 1) % 3);
    // curve.append(ClothoidTransition.of(p1, flip(p2)).linearized(width));
    // // curve.append(DOMAIN.map(Se2Clothoids.INSTANCE.curve(p1, flip(p2))));
    // }
    // curve = Flatten.of(curve, 1);
    Tensor curve = Spearhead.of(control.get(0), width);
    graphics.setColor(COLOR_DATA_INDEXED.getColor(1));
    Path2D path2d = geometricLayer.toPath2D(curve, true);
    graphics.fill(path2d);
    new PathRender(COLOR_DATA_INDEXED.getColor(0), 1.5f) //
        .setCurve(curve, false) //
        .render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new SpearheadDemo().setVisible(1000, 800);
  }
}
