// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;

/** rendering of grid lines parallel to x and y axes */
public enum GridRender implements RenderInterface {
  INSTANCE;
  // ---
  private static final Scalar LO = DoubleScalar.of(-50);
  private static final Scalar HI = DoubleScalar.of(+50);
  private static final Color COLOR = new Color(224, 224, 224, 128);

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(COLOR);
    for (Tensor x : Subdivide.of(LO, HI, 10)) {
      graphics.draw(new Line2D.Double( //
          geometricLayer.toPoint2D(Tensors.of(LO, x)), //
          geometricLayer.toPoint2D(Tensors.of(HI, x))));
      graphics.draw(new Line2D.Double( //
          geometricLayer.toPoint2D(Tensors.of(x, LO)), //
          geometricLayer.toPoint2D(Tensors.of(x, HI))));
    }
  }
}
