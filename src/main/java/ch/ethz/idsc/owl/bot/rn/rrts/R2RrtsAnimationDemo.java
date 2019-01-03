// code by jph
package ch.ethz.idsc.owl.bot.rn.rrts;

import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.glc.adapter.GoalConsumer;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

// not available at the moment
class R2RrtsAnimationDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    R2RrtsEntity r2RrtsEntity = new R2RrtsEntity(Tensors.vector(0, 0));
    // TODO create noise1 obstacle regions
    // r2RrtsEntity.obstacleQuery = StaticHelper.noise1();
    GoalConsumer goalConsumer = new GoalConsumer() {
      @Override
      public void accept(Tensor goal) {
        // RrtsPlanner
        // r2RrtsEntity.createTrajectoryPlanner(plannerConstraint, goal);
      }
    };
    MouseGoal.supply(owlyAnimationFrame.geometricComponent, goalConsumer);
    owlyAnimationFrame.add(r2RrtsEntity);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new R2RrtsAnimationDemo().start().jFrame.setVisible(true);
  }
}
