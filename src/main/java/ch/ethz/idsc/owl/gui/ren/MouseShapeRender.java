// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class MouseShapeRender implements RenderInterface {
  private final Region<StateTime> region;
  private final Tensor shape;
  private final Supplier<Scalar> supplier;

  public MouseShapeRender(Region<StateTime> region, Tensor shape, Supplier<Scalar> supplier) {
    this.region = region;
    this.shape = shape;
    this.supplier = supplier;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor xya = geometricLayer.getMouseSe2State();
    StateTime stateTime = new StateTime(xya, supplier.get());
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
    Color color = region.isMember(stateTime) //
        ? new Color(255, 96, 96, 128)
        : new Color(0, 128, 255, 192);
    graphics.setColor(color);
    graphics.fill(geometricLayer.toPath2D(shape));
    geometricLayer.popMatrix();
  }
}
