// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.ani.AnimationInterface;
import ch.ethz.idsc.owl.gui.ani.RrtsPlannerCallback;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Tensor;

@Deprecated // class preserves rrts callback until functionality is reproduced
class MousePlanner extends MouseAdapter {
  private MotionPlanWorker mpw = null;
  // ---
  AnimationInterface controllable;
  GeometricComponent geometricComponent;
  RrtsPlannerCallback rrtsPlannerCallback;

  @Override
  public void mouseClicked(MouseEvent mouseEvent) {
    final int mods = mouseEvent.getModifiersEx();
    final int mask = MouseWheelEvent.CTRL_DOWN_MASK; // 128 = 2^7
    if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
      if ((mods & mask) == 0) { // no ctrl pressed
        if (Objects.nonNull(mpw)) {
          mpw.flagShutdown();
          mpw = null;
        }
        if (controllable instanceof TrajectoryEntity) {
          TrajectoryEntity abstractEntity = (TrajectoryEntity) controllable;
          final Tensor goal = geometricComponent.getMouseSe2State();
          final List<TrajectorySample> head = //
              abstractEntity.getFutureTrajectoryUntil(abstractEntity.delayHint());
          // switch (abstractEntity.getPlannerType()) {
          // case RRTS: {
          // AbstractRrtsEntity abstractRrtsEntity = (AbstractRrtsEntity) abstractEntity;
          // abstractRrtsEntity.startPlanner(rrtsPlannerCallback, head, goal);
          // break;
          // }
          // default:
          // throw new RuntimeException();
          // }
        }
      }
    }
  }
}
