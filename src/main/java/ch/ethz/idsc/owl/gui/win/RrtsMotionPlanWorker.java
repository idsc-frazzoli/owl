// code by jph, gjoel
package ch.ethz.idsc.owl.gui.win;

import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.ani.api.RrtsPlannerCallback;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.RrtsPlannerServer;
import ch.ethz.idsc.tensor.io.Timing;

/** TODO combine with {@link MotionPlanWorker}
 * might require combination of {@link RrtsPlannerCallback} and {@link GlcPlannerCallback} */
public class RrtsMotionPlanWorker {
  private final int maxSteps;
  private final Collection<RrtsPlannerCallback> rrtsPlannerCallbacks;
  // ---
  private volatile boolean isRelevant = true;

  public RrtsMotionPlanWorker(int maxSteps, Collection<RrtsPlannerCallback> rrtsPlannerCallbacks) {
    this.maxSteps = maxSteps;
    this.rrtsPlannerCallbacks = rrtsPlannerCallbacks;
  }

  /** the planner motion plans from the last {@link StateTime} in head
   * 
   * @param head non-empty trajectory
   * @param rrtsPlannerServer */
  public void start(List<TrajectorySample> head, RrtsPlannerServer rrtsPlannerServer) {
    Thread thread = new Thread(new Runnable() {
      @Override // from Runnable
      public void run() {
        Timing timing = Timing.started();
        StateTime root = Lists.getLast(head).stateTime(); // last statetime in head trajectory
        rrtsPlannerServer.insertRoot(root);
        rrtsPlannerServer.getProcess().ifPresent(process -> process.run(maxSteps));
        if (isRelevant) {
          timing.seconds();
          for (RrtsPlannerCallback rrtsPlannerCallback : rrtsPlannerCallbacks)
            rrtsPlannerCallback.expandResult(head, rrtsPlannerServer);
        }
      }
    });
    thread.start();
  }

  public void flagShutdown() {
    isRelevant = false;
  }
}
