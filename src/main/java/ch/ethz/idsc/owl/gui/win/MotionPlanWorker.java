// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.rl.GlcRLExpand;
import ch.ethz.idsc.owl.glc.rl.RLTrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.io.Timing;

public class MotionPlanWorker {
  private final int maxSteps;
  private final Collection<GlcPlannerCallback> glcPlannerCallbacks;
  // ---
  private volatile boolean isRelevant = true;

  public MotionPlanWorker(int maxSteps, Collection<GlcPlannerCallback> glcPlannerCallbacks) {
    this.maxSteps = maxSteps;
    this.glcPlannerCallbacks = glcPlannerCallbacks;
  }

  /** the planner motion plans from the last {@link StateTime} in head
   * 
   * @param head non-empty trajectory
   * @param trajectoryPlanner */
  public void start(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    Thread thread = new Thread(new Runnable() {
      @Override // from Runnable
      public void run() {
        Timing timing = Timing.started();
        StateTime root = Lists.getLast(head).stateTime(); // last statetime in head trajectory
        trajectoryPlanner.insertRoot(root);
        GlcExpand glcExpand = trajectoryPlanner instanceof RLTrajectoryPlanner //
            ? new GlcRLExpand(trajectoryPlanner)
            : new GlcExpand(trajectoryPlanner);
        glcExpand.setContinued(() -> isRelevant);
        glcExpand.findAny(maxSteps);
        if (isRelevant) {
          timing.seconds();
          for (GlcPlannerCallback glcPlannerCallback : glcPlannerCallbacks)
            glcPlannerCallback.expandResult(head, trajectoryPlanner);
        }
      }
    });
    thread.start();
  }

  public void flagShutdown() {
    isRelevant = false;
  }
}
