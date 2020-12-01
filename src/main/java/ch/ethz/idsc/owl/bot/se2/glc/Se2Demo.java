// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;

public abstract class Se2Demo implements DemoInterface {
  @Override
  public final OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    owlyAnimationFrame.geometricComponent.setOffset(50, 700);
    owlyAnimationFrame.jFrame.setTitle(getClass().getSimpleName());
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    configure(owlyAnimationFrame);
    return owlyAnimationFrame;
  }

  protected abstract void configure(OwlyAnimationFrame owlyAnimationFrame);
}
