// code by jph
package ch.ethz.idsc.owl.gui.ani;

import java.util.List;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

// EXPERIMENTAL API not finalized
public class MotionPlanWorker {
  private TrajectoryPlannerCallback trajectoryPlannerCallback;
  private Thread thread;
  private volatile boolean isRelevant = true;

  public MotionPlanWorker(TrajectoryPlannerCallback trajectoryPlannerCallback) {
    this.trajectoryPlannerCallback = trajectoryPlannerCallback;
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
        Expand.maxSteps(trajectoryPlanner, 5000, () -> isRelevant); // magic const
        if (isRelevant) {
          Scalar duration = RealScalar.of(stopwatch.display_seconds());
          System.out.println("planning: " + Quantity.of((Scalar) duration.map(Round._3), "s"));
          trajectoryPlannerCallback.expandResult(head, trajectoryPlanner);
        }
      }
    });
    thread.start();
  }

  public void flagShutdown() {
    isRelevant = false;
  }
}
