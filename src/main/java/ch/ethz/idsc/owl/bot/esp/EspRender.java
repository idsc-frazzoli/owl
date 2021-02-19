// code by jph
package ch.ethz.idsc.owl.bot.esp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;

/* package */ class EspRender implements RenderInterface {
  private static final Tensor CIRCLE = CirclePoints.of(31).multiply(RealScalar.of(0.48));
  private static final Color LIGHT = new Color(188, 169, 80);
  private static final Color DARK = new Color(63, 54, 14);
  private static final Color EMPTY = new Color(203, 203, 203);
  // ---
  private final Tensor board;

  public EspRender(Tensor board) {
    this.board = Objects.requireNonNull(board);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    for (int px = 0; px < 5; ++px)
      for (int py = 0; py < 5; ++py)
        if (EspFlows.isField(px, py)) {
          int value = Scalars.intValueExact(board.Get(px, py));
          // System.out.println(px + " " + value);
          switch (value) {
          case 0:
            graphics.setColor(EMPTY);
            break;
          case 1:
            graphics.setColor(LIGHT);
            break;
          case 2:
            graphics.setColor(DARK);
            break;
          default:
            throw new RuntimeException();
          }
          geometricLayer.pushMatrix(Se2Matrix.translation(Tensors.vector(px + 0.5, py + 0.5)));
          Path2D path2d = geometricLayer.toPath2D(CIRCLE);
          graphics.fill(path2d);
          geometricLayer.popMatrix();
        }
  }
}
