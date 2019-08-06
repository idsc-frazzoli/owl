// code by gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.lane.LaneConsumer;
import ch.ethz.idsc.owl.math.lane.LaneInterface;
import ch.ethz.idsc.owl.math.lane.LaneRandomSample;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.math.sample.ConstantRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.sophus.math.sample.RegionRandomSample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.qty.Degree;

public abstract class LaneRrtsPlannerServer extends DefaultRrtsPlannerServer implements LaneConsumer {
  private static final Distribution DEFAULT_ROT_DIST = NormalDistribution.of(RealScalar.ZERO, Degree.of(5));
  // ---
  private final boolean greedy;
  private RandomSampleInterface laneSampler;
  private RandomSampleInterface goalSampler;
  private Distribution rotDist = DEFAULT_ROT_DIST;

  public LaneRrtsPlannerServer( //
      TransitionSpace transitionSpace, //
      TransitionRegionQuery obstacleQuery, //
      Scalar resolution, //
      StateSpaceModel stateSpaceModel, //
      boolean greedy) {
    super(transitionSpace, obstacleQuery, resolution, stateSpaceModel);
    this.greedy = greedy;
  }

  public LaneRrtsPlannerServer( //
      TransitionSpace transitionSpace, //
      TransitionRegionQuery obstacleQuery, //
      Scalar resolution, //
      StateSpaceModel stateSpaceModel, //
      TransitionCostFunction costFunction, //
      boolean greedy) {
    super(transitionSpace, obstacleQuery, resolution, stateSpaceModel, costFunction);
    this.greedy = greedy;
  }

  @Override // from DefaultRrtsPlannerServer
  protected RandomSampleInterface spaceSampler(Tensor state) {
    if (Objects.nonNull(laneSampler))
      return laneSampler;
    return new ConstantRandomSample(state);
  }

  @Override // from DefaultRrtsPlannerServer
  protected RandomSampleInterface goalSampler(Tensor state) {
    if (Objects.nonNull(goalSampler))
      return goalSampler;
    return new ConstantRandomSample(state);
  }

  @Override // from Consumer
  public void accept(LaneInterface laneInterface) {
    laneSampler = LaneRandomSample.along(laneInterface, rotDist);
    goalSampler = LaneRandomSample.endSample(laneInterface, rotDist);
        // LaneRandomSample.endSample(laneInterface, rotDist, RealScalar.of(10), Degree.of(25));
    if (greedy)
      setGreeds(laneInterface.controlPoints().stream().collect(Collectors.toList()));
  }

  public Optional<Region<Tensor>> goalRegion() {
    if (goalSampler instanceof RegionRandomSample)
      return Optional.of(((RegionRandomSample) goalSampler).region());
    return Optional.empty();
  }

  public void setRotationDistribution(Distribution distribution) {
    rotDist = distribution;
  }
}
