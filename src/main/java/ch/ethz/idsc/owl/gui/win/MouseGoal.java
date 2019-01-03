// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.GoalConsumer;
import ch.ethz.idsc.owl.glc.adapter.SimpleGlcPlannerCallback;
import ch.ethz.idsc.owl.glc.adapter.SimpleGoalConsumer;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.ani.GlcPlannerCallback;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;

public enum MouseGoal {
  ;
  public static void simple( //
      OwlyAnimationFrame owlyAnimationFrame, TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint) {
    simple(owlyAnimationFrame, trajectoryEntity, plannerConstraint, Collections.emptyList());
  }

  public static void simple( //
      OwlyAnimationFrame owlyAnimationFrame, TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint, //
      List<GlcPlannerCallback> _callbacks) {
    List<GlcPlannerCallback> callbacks = new ArrayList<>(_callbacks);
    if (trajectoryEntity instanceof GlcPlannerCallback)
      callbacks.add((GlcPlannerCallback) trajectoryEntity);
    callbacks.add(new SimpleGlcPlannerCallback(trajectoryEntity));
    supply(owlyAnimationFrame.geometricComponent, //
        new SimpleGoalConsumer(trajectoryEntity, plannerConstraint, callbacks));
  }

  public static void supply(GeometricComponent geometricComponent, GoalConsumer goalConsumer) {
    MouseAdapter mouseAdapter = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent mouseEvent) {
        final int mods = mouseEvent.getModifiersEx();
        final int mask = MouseWheelEvent.CTRL_DOWN_MASK; // 128 = 2^7
        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
          if ((mods & mask) == 0) // no ctrl pressed
            goalConsumer.accept(geometricComponent.getMouseSe2State());
        }
      }
    };
    geometricComponent.jComponent.addMouseListener(mouseAdapter);
  }
}
