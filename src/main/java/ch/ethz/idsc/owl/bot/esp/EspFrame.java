// code by jph
package ch.ethz.idsc.owl.bot.esp;

import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gui.win.AbstractDemo;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class EspFrame extends AbstractDemo {
  Tensor _board = null;

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    Tensor board = _board;
    if (Objects.nonNull(board)) {
      new EspRender(board).render(geometricLayer, graphics);
    }
  }
}
