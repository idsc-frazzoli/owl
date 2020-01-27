// code by jph
package ch.ethz.idsc.owl.bot.esp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.CirclePoints;

class EspRender implements RenderInterface {
  private static final Tensor CIRCLE = CirclePoints.of(15).multiply(RationalScalar.HALF);
  // ---
  private final Tensor board;

  public EspRender(Tensor board) {
    this.board = Objects.requireNonNull(board);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    for (int px = 0; px < 5; ++px)
      for (int py = 0; py < 5; ++py)
        if (EspModel.isField(px, py)) {
          int value = board.Get(px, py).number().intValue();
          // System.out.println(px + " " + value);
          switch (value) {
          case 0:
            graphics.setColor(Color.BLACK);
            break;
          case 1:
            graphics.setColor(Color.RED);
            break;
          case 2:
            graphics.setColor(Color.BLUE);
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
