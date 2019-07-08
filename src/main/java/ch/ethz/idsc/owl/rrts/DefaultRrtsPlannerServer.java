// code by jph, gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
import ch.ethz.idsc.owl.rrts.core.DefaultRrtsPlanner;
import ch.ethz.idsc.owl.rrts.core.Rrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.RrtsPlanner;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public abstract class DefaultRrtsPlannerServer extends RrtsPlannerServer {
  private Tensor state = Tensors.empty();
  private Tensor goal = Tensors.empty();

  public DefaultRrtsPlannerServer( //
      TransitionSpace transitionSpace, //
      TransitionRegionQuery obstacleQuery, //
      Scalar resolution, //
      StateSpaceModel stateSpaceModel) {
    this(transitionSpace, obstacleQuery, resolution, stateSpaceModel, LengthCostFunction.IDENTITY);
  }

  public DefaultRrtsPlannerServer( //
      TransitionSpace transitionSpace, //
      TransitionRegionQuery obstacleQuery, //
      Scalar resolution, //
      StateSpaceModel stateSpaceModel, //
      TransitionCostFunction costFunction) {
    super(transitionSpace, obstacleQuery,resolution, stateSpaceModel, costFunction);
  }

  @Override // from RrtsPlannerServer
  public void setState(StateTime stateTime) {
    super.setState(stateTime);
    state = stateTime.state();
  }

  @Override // from RrtsPlannerServer
  public void setGoal(Tensor goal) {
    this.goal = goal;
  }

  @Override // from RrtsPlannerServer
  protected RrtsPlannerProcess setupProcess(StateTime stateTime) {
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection(), obstacleQuery, costFunction);
    Optional<RrtsNode> optional = rrts.insertAsNode(Objects.requireNonNull(stateTime).state(), 5);
    if (optional.isPresent()) {
      RrtsPlanner rrtsPlanner = new DefaultRrtsPlanner(rrts, spaceSampler(state), goalSampler(goal));
      return new RrtsPlannerProcess(rrtsPlanner, optional.get());
    }
    return null;
  }

  protected abstract RrtsNodeCollection rrtsNodeCollection();

  protected abstract RandomSampleInterface spaceSampler(Tensor state);

  protected abstract RandomSampleInterface goalSampler(Tensor state);
}
