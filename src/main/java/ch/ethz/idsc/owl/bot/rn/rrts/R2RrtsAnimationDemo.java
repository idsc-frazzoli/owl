// code by jph
package ch.ethz.idsc.owl.bot.rn.rrts;

import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.ani.OwlyAnimationFrame;
import ch.ethz.idsc.tensor.Tensors;

public class R2RrtsAnimationDemo implements DemoInterface {
  @Override
  public void start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    R2RrtsEntity r2RrtsEntity = new R2RrtsEntity(Tensors.vector(0, 0));
    r2RrtsEntity.obstacleQuery = StaticHelper.noise1();
    owlyAnimationFrame.set(r2RrtsEntity);
    owlyAnimationFrame.jFrame.setVisible(true);
  }

  public static void main(String[] args) {
    new R2RrtsAnimationDemo().start();
  }
}
