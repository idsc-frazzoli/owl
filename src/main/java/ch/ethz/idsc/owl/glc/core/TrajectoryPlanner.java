// code by ynager
package ch.ethz.idsc.owl.glc.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

public interface TrajectoryPlanner extends ExpandInterface<GlcNode>, Serializable {
  void insertRoot(StateTime stateTime);

  public Optional<GlcNode> getBest();

  public Optional<GlcNode> getBestOrElsePeek();

  public StateIntegrator getStateIntegrator();

  public HeuristicFunction getHeuristicFunction();

  public Map<Tensor, GlcNode> getDomainMap();

  public Collection<GlcNode> getQueue();
}
