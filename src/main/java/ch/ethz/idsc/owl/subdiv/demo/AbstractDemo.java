// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.TimerFrame;

abstract class AbstractDemo implements RenderInterface {
  final TimerFrame timerFrame = new TimerFrame();

  public AbstractDemo() {
    timerFrame.jFrame.setTitle(getClass().getSimpleName());
    timerFrame.geometricComponent.addRenderInterface(this);
  }
}
