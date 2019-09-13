// code by gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.lane.LaneConsumer;
import ch.ethz.idsc.owl.math.lane.LaneEndSamples;
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

// TODO JPH OWL 055 move to se2 specific package
// TODO don't use magic constants at all. implement via interface, make interface final 
public abstract class LaneRrtsPlannerServer extends DefaultRrtsPlannerServer implements LaneConsumer {
  private static final Distribution DEFAULT_ROT_DIST = NormalDistribution.of(RealScalar.ZERO, Degree.of(5));
  // ---
  private final boolean greedy;
  private RandomSampleInterface laneSampler;
  private RandomSampleInterface goalSampler;
  // ---
  private boolean conical = false;
  private Distribution rotDist = DEFAULT_ROT_DIST;
  private Scalar mu_r = RealScalar.of(3);
  private Scalar semi = Degree.of(17);

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
  protected final RandomSampleInterface spaceSampler(Tensor state) {
    // TODO document why laneSampler might not be "ready" to be returned
    return Objects.nonNull(laneSampler) //
        ? laneSampler
        : new ConstantRandomSample(state);
  }

  @Override // from DefaultRrtsPlannerServer
  protected final RandomSampleInterface goalSampler(Tensor state) {
    return Objects.nonNull(goalSampler) //
        ? goalSampler
        : new ConstantRandomSample(state);
  }

  @Override // from Consumer
  public final void accept(LaneInterface laneInterface) {
    laneSampler = LaneRandomSample.of(laneInterface, rotDist);
    goalSampler = conical //
        ? LaneEndSamples.cone(laneInterface, rotDist, mu_r, semi) //
        : LaneEndSamples.endSample(laneInterface, rotDist);
    if (greedy)
      setGreeds(laneInterface.controlPoints().stream().collect(Collectors.toList()));
  }

  public final Optional<Region<Tensor>> goalRegion() {
    if (goalSampler instanceof RegionRandomSample) {
      RegionRandomSample regionRandomSample = (RegionRandomSample) goalSampler;
      return Optional.of(regionRandomSample.region());
    }
    return Optional.empty();
  }

  public final void setConical(boolean conical) {
    this.conical = conical;
  }

  public final void setRotationDistribution(Distribution distribution) {
    rotDist = distribution;
  }

  public final void setCone(Scalar mu_r, Scalar semi) {
    this.mu_r = mu_r;
    this.semi = semi;
  }
}
