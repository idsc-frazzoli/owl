// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.ani.AbstractRrtsEntity;
import ch.ethz.idsc.owl.gui.ani.AnimationInterface;
import ch.ethz.idsc.owl.gui.ani.GlcTrajectoryPlannerCallback;
import ch.ethz.idsc.owl.gui.ani.RrtsTrajectoryPlannerCallback;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Tensor;

public class MousePlanner extends MouseAdapter {
  private MotionPlanWorker mpw = null;
  // ---
  AnimationInterface controllable;
  GeometricComponent geometricComponent;
  PlannerConstraint plannerConstraint;
  GlcTrajectoryPlannerCallback trajectoryPlannerCallback;
  RrtsTrajectoryPlannerCallback rrtsTrajectoryPlannerCallback;

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
          switch (abstractEntity.getPlannerType()) {
          case STANDARD: {
            TrajectoryPlanner trajectoryPlanner = //
                abstractEntity.createTrajectoryPlanner(plannerConstraint, goal);
            mpw = new MotionPlanWorker();
            mpw.addCallback(trajectoryPlannerCallback);
            mpw.start(head, trajectoryPlanner);
            break;
          }
          case RRTS: {
            AbstractRrtsEntity abstractRrtsEntity = (AbstractRrtsEntity) abstractEntity;
            abstractRrtsEntity.startPlanner(rrtsTrajectoryPlannerCallback, head, goal);
            break;
          }
          default:
            throw new RuntimeException();
          }
        }
      }
    }
  }
}
