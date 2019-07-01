// code by jph, gjoel
package ch.ethz.idsc.owl.gui.win;

import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.ani.api.PlannerCallback;
import ch.ethz.idsc.owl.ani.api.TrajectoryPlanner;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.adapter.SimpleGoalConsumer;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.adapter.RrtsGoalConsumer;
import ch.ethz.idsc.tensor.io.Timing;

/** TODO is generic superclass necessary, or could this be solved in one
 * see e.g. {@link SimpleGoalConsumer} and {@link RrtsGoalConsumer}
 * @param <T>
 * @param <P> */
public abstract class MotionPlanWorker<T extends TrajectoryPlanner, P extends PlannerCallback<T>> {
  protected final int maxSteps;
  private final Collection<P> plannerCallbacks;
  // ---
  protected volatile boolean isRelevant = true;

  public MotionPlanWorker(int maxSteps, Collection<P> plannerCallbacks) {
    this.maxSteps = maxSteps;
    this.plannerCallbacks = plannerCallbacks;
  }

  /** the planner motion plans from the last {@link StateTime} in head
   * 
   * @param head non-empty trajectory
   * @param trajectoryPlanner */
  public void start(List<TrajectorySample> head, T trajectoryPlanner) {
    Thread thread = new Thread(new Runnable() {
      @Override // from Runnable
      public void run() {
        Timing timing = Timing.started();
        StateTime root = Lists.getLast(head).stateTime(); // last statetime in head trajectory
        trajectoryPlanner.insertRoot(root);
        expand(trajectoryPlanner);
        if (isRelevant) {
          timing.seconds();
          for (P plannerCallback : plannerCallbacks)
            plannerCallback.expandResult(head, trajectoryPlanner);
        }
      }
    });
    thread.start();
  }

  public void flagShutdown() {
    isRelevant = false;
  }

  protected abstract void expand(T trajectoryPlanner);
}
