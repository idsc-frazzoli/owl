// code by gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.Objects;

import ch.ethz.idsc.owl.math.Lane;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.sample.ConstantRandomSample;
import ch.ethz.idsc.owl.math.sample.LaneRandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.owl.rrts.adapter.LaneConsumer;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public abstract class LaneRrtsPlannerServer extends DefaultRrtsPlannerServer implements LaneConsumer {
  private LaneRandomSample laneSampler;

  public LaneRrtsPlannerServer( //
      TransitionSpace transitionSpace, //
      TransitionRegionQuery obstacleQuery, //
      Scalar resolution, //
      StateSpaceModel stateSpaceModel) {
    super(transitionSpace, obstacleQuery, resolution, stateSpaceModel);
  }

  public LaneRrtsPlannerServer( //
      TransitionSpace transitionSpace, //
      TransitionRegionQuery obstacleQuery, //
      Scalar resolution, //
      StateSpaceModel stateSpaceModel, //
      TransitionCostFunction costFunction) {
    super(transitionSpace, obstacleQuery, resolution, stateSpaceModel, costFunction);
  }

  @Override // from DefaultRrtsPlannerServer
  protected RandomSampleInterface spaceSampler(Tensor state) {
    if (Objects.nonNull(laneSampler))
      return laneSampler;
    return new ConstantRandomSample(state);
  }

  @Override // from DefaultRrtsPlannerServer
  protected RandomSampleInterface goalSampler(Tensor state) {
    if (Objects.nonNull(laneSampler))
      return laneSampler.endSample();
    return new ConstantRandomSample(state);
  }

  @Override // from Consumer
  public void accept(Lane lane) {
    laneSampler = LaneRandomSample.along(lane);
  }
}
