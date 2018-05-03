// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.GlcPlannerCallback;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class MotionPlanWorker {
  private static final int MAX_STEPS = 5000; // FIXME magic const
  // ---
  private final List<GlcPlannerCallback> glcPlannerCallbacks = new LinkedList<>();
  private Thread thread;
  private volatile boolean isRelevant = true;

  public void addCallback(GlcPlannerCallback glcPlannerCallback) {
    glcPlannerCallbacks.add(glcPlannerCallback);
  }

  /** the planner motion plans from the last {@link StateTime} in head
   * 
   * @param head non-empty trajectory
   * @param trajectoryPlanner */
  public void start(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    thread = new Thread(new Runnable() {
      @Override
      public void run() {
        Stopwatch stopwatch = Stopwatch.started();
        StateTime root = Lists.getLast(head).stateTime(); // last statetime in head trajectory
        trajectoryPlanner.insertRoot(root);
        Expand.maxSteps(trajectoryPlanner, MAX_STEPS, () -> isRelevant);
        if (isRelevant) {
          Scalar duration = RealScalar.of(stopwatch.display_seconds());
          // System.out.println("planning: " + Quantity.of((Scalar) duration.map(Round._3), "s"));
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
