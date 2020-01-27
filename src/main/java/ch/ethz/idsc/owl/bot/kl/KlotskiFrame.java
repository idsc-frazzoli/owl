// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.owl.bot.kl.KlotskiPlot.Plot;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.tensor.Tensor;

class KlotskiFrame extends AbstractDemo {
  private final KlotskiPlot klotskiPlot;
  Tensor _board = null;

  public KlotskiFrame(KlotskiProblem klotskiProblem, Tensor border) {
    klotskiPlot = new KlotskiPlot(klotskiProblem, border);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor board = _board;
    if (Objects.nonNull(board)) {
      Plot plot = klotskiPlot.new Plot(board);
      plot.render(geometricLayer, graphics);
    }
  }
}
