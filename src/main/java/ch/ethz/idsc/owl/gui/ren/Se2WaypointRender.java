// code by ynager, jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;

/** draws a given shape at frames */
public class Se2WaypointRender implements RenderInterface {
  private final Tensor frames;
  private final Tensor shape;
  private final Color color;

  /** @param frames with dimensions n x 3 where each row corresponds to {x, y, angle}
   * @param shape with dimensions m x 2
   * @param color */
  public Se2WaypointRender(Tensor frames, Tensor shape, Color color) {
    this.frames = Objects.requireNonNull(frames);
    this.shape = shape;
    this.color = color;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(color);
    for (Tensor xya : frames) { // draw frame as arrow
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
      graphics.fill(geometricLayer.toPath2D(shape));
      geometricLayer.popMatrix();
    }
  }
}
