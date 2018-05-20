// code by jph
package ch.ethz.idsc.owl.bot.rn.rrts;

import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.tensor.Tensors;

public class R2RrtsAnimationDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    R2RrtsEntity r2RrtsEntity = new R2RrtsEntity(Tensors.vector(0, 0));
    r2RrtsEntity.obstacleQuery = StaticHelper.noise1();
    owlyAnimationFrame.add(r2RrtsEntity);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new R2RrtsAnimationDemo().start().jFrame.setVisible(true);
  }
}
