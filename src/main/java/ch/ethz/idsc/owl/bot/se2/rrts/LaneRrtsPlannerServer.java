// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.lane.LaneConsumer;
import ch.ethz.idsc.owl.lane.LaneInterface;
import ch.ethz.idsc.owl.lane.LaneRandomSample;
import ch.ethz.idsc.owl.lane.Se2ConeRandomSample;
import ch.ethz.idsc.owl.lane.Se2SphereRandomSample;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.owl.math.region.BallRegion;
import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.rrts.DefaultRrtsPlannerServer;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.sophus.math.sample.ConstantRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.qty.Degree;

// TODO don't use magic constants at all. implement via interface, make interface final 
public abstract class LaneRrtsPlannerServer extends DefaultRrtsPlannerServer implements LaneConsumer {
  private static final Distribution DEFAULT_ROT_DIST = NormalDistribution.of(RealScalar.ZERO, Degree.of(5));
  // ---
  private final boolean greedy;
  private RandomSampleInterface laneSampler;
  private RandomSampleInterface goalSampler;
  private Region<Tensor> goalRegion;
  // ---
  private boolean conical = false;
  private Scalar mu_r = RealScalar.of(3);
  private Scalar semi = Degree.of(17);
  private Scalar heading = Degree.of(5);
  private Distribution rotDist = DEFAULT_ROT_DIST;

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
    final Tensor apex = Last.of(laneInterface.midLane());
    if (conical) {
      goalSampler = Se2ConeRandomSample.of(apex, semi, heading, mu_r);
      goalRegion = new ConeRegion(apex, semi);
    } else {
      Scalar radius = Last.of(laneInterface.margins());
      goalSampler = new Se2SphereRandomSample(apex, radius, rotDist);
      goalRegion = new BallRegion(Extract2D.FUNCTION.apply(apex), radius);
    }
    if (greedy)
      setGreeds(laneInterface.controlPoints().stream().collect(Collectors.toList()));
  }

  public final Optional<Region<Tensor>> goalRegion() {
    return Optional.ofNullable(goalRegion);
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
