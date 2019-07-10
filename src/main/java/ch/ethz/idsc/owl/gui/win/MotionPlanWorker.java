// code by jph, gjoel
package ch.ethz.idsc.owl.gui.win;

import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.ani.api.PlannerCallback;
import ch.ethz.idsc.owl.ani.api.RrtsPlannerCallback;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.data.tree.TreePlanner;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.io.Timing;

public abstract class MotionPlanWorker<T extends TreePlanner<?>, P extends PlannerCallback<T>> {
  @SuppressWarnings("unchecked")
  public static MotionPlanWorker of(int maxSteps, Collection<? extends PlannerCallback> plannerCallbacks) {
    if (plannerCallbacks.stream().allMatch(p -> p instanceof GlcPlannerCallback))
      return new GlcMotionPlanWorker(maxSteps, (Collection<GlcPlannerCallback>) plannerCallbacks);
    if (plannerCallbacks.stream().allMatch(p -> p instanceof RrtsPlannerCallback))
      return new RrtsMotionPlanWorker(maxSteps, (Collection<RrtsPlannerCallback>) plannerCallbacks);
    // TODO expand when needed
    throw new IllegalArgumentException();
  }

  protected final int maxSteps;
  private final Collection<P> plannerCallbacks;
  // ---
  protected volatile boolean isRelevant = true;

  protected MotionPlanWorker(int maxSteps, Collection<P> plannerCallbacks) {
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
