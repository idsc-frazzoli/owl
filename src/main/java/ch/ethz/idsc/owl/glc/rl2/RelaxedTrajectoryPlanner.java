// code by astoll, ynager
package ch.ethz.idsc.owl.glc.rl2;

import java.io.Serializable;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.HeuristicFunction;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.tensor.Tensor;

public abstract class RelaxedTrajectoryPlanner implements TrajectoryPlanner, Serializable {
  protected final StateTimeRaster stateTimeRaster;
  private final HeuristicFunction heuristicFunction;
  private final Tensor slacks;

  // ---
  protected RelaxedTrajectoryPlanner(StateTimeRaster stateTimeRaster, HeuristicFunction heuristicFunction, Tensor slacks) {
    this.slacks = slacks;
    this.stateTimeRaster = stateTimeRaster;
    this.heuristicFunction = heuristicFunction;
  }

  /** @param domain_key
   * @param node non-null
   * @return true if node is added to open queue and domain queue */
  protected final void addToOpen(Tensor domain_key, GlcNode node) {
    ;
  }

  protected final void addToDomainMap(Tensor domain_key, GlcNode node) {
    ;
  }
}
