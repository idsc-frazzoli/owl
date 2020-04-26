// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.N;

/* package */ enum S1FrameRender implements RenderInterface {
  INSTANCE;

  private static final Tensor BOX = Tensors.fromString("{{-1, -1}, {1, -1}, {1, 1}, {-1, 1}}").multiply(RealScalar.of(2.5));
  private static final Tensor CIRCLE = CirclePoints.of(61).map(N.DOUBLE);

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setDefault(graphics);
    Path2D path2d = geometricLayer.toPath2D(BOX, true);
    graphics.setColor(Color.LIGHT_GRAY);
    graphics.draw(path2d);
    RenderQuality.setQuality(graphics);
    graphics.setColor(Color.LIGHT_GRAY);
    graphics.draw(geometricLayer.toPath2D(CIRCLE, true));
  }
}
