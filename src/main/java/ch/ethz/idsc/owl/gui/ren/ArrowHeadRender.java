// code by ynager, jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class ArrowHeadRender implements RenderInterface {
  private static final Tensor SHAPE = Tensors.matrixDouble( //
      new double[][] { { .3, 0 }, { -.1, -.1 }, { -.1, +.1 } }).unmodifiable();
  // ---
  private Tensor frames;
  private Color color;

  public ArrowHeadRender(Tensor points, Color color) {
    this.frames = Objects.requireNonNull(points);
    this.color = color;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(color);
    for (Tensor xya : frames) { // draw frame as arrow
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
      graphics.fill(geometricLayer.toPath2D(SHAPE));
      geometricLayer.popMatrix();
    }
  }
}
