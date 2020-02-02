// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class KlotskiFrame extends AbstractDemo {
  private final KlotskiPlot klotskiPlot;
  // TODO bad design
  Tensor _board = null;

  public KlotskiFrame(KlotskiProblem klotskiProblem) {
    klotskiPlot = new KlotskiPlot(klotskiProblem);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor board = _board;
    if (Objects.nonNull(board))
      klotskiPlot.new Plot(board).render(geometricLayer, graphics);
  }
}
