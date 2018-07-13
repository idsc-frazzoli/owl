// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.owl.glc.std.GoalConsumer;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.SimpleGlcPlannerCallback;
import ch.ethz.idsc.owl.glc.std.SimpleGoalConsumer;
import ch.ethz.idsc.owl.gui.ani.GlcPlannerCallback;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;

public enum MouseGoal {
  ;
  public static void simple(OwlyAnimationFrame owlyAnimationFrame, TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint) {
    List<GlcPlannerCallback> list = new ArrayList<>();
    if (trajectoryEntity instanceof GlcPlannerCallback)
      list.add((GlcPlannerCallback) trajectoryEntity);
    list.add(new SimpleGlcPlannerCallback(trajectoryEntity));
    supply(owlyAnimationFrame.geometricComponent, //
        new SimpleGoalConsumer(trajectoryEntity, plannerConstraint, list));
  }

  private static void supply(GeometricComponent geometricComponent, GoalConsumer goalConsumer) {
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
