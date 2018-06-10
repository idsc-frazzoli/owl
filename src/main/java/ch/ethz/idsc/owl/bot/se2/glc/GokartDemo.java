// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

abstract class GokartDemo implements DemoInterface {
  static final Tensor ARROWHEAD = Tensors.matrixDouble( //
      new double[][] { { .3, 0 }, { -.1, -.1 }, { -.1, +.1 } }).multiply(RealScalar.of(2));

  abstract void configure(OwlyAnimationFrame owlyAnimationFrame);

  @Override
  public final OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    configure(owlyAnimationFrame);
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlyAnimationFrame;
  }
}
