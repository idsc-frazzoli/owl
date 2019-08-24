// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.util.Collections;

import ch.ethz.idsc.owl.ani.adapter.FallbackControl;
import ch.ethz.idsc.owl.ani.api.AbstractRrtsEntity;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.bot.se2.glc.CarEntity;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.lane.LaneConsumer;
import ch.ethz.idsc.owl.math.lane.LaneInterface;
import ch.ethz.idsc.owl.math.lane.StableLanes;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.rrts.LaneRrtsPlannerServer;
import ch.ethz.idsc.owl.rrts.RrtsNodeCollections;
import ch.ethz.idsc.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.SimpleLaneConsumer;
import ch.ethz.idsc.owl.rrts.adapter.TransitionRegionQueryUnion;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid3;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.Pi;

/* package */ enum ClothoidLaneSimulation {
  ;

  private static final Tensor[] CONTROLS = { // TODO GJOEL fill in
      Tensors.fromString("{{6.017, 4.983, 0.785},{8.100, 5.100, -1.571},{1.667, 1.950, -3.142}}")};
  private static final int REPS = 10;
  private static final Scalar DELAY_HINT = RealScalar.of(3);
  private static final Scalar OVERHEAD = RealScalar.of(.5);
  // ---
  private static final GeodesicInterface GEODESIC_INTERFACE = Clothoid3.INSTANCE;
  private static final int DEGREE = 3;
  private static final int LEVELS = 5;
  private static final Scalar LANE_WIDTH = RealScalar.of(1.1);
  // ---
  private static final R2ImageRegionWrap R2_IMAGE_REGION_WRAP = R2ImageRegions._GTOB;
  private static final TransitionRegionQuery TRANSITION_REGION_QUERY = TransitionRegionQueryUnion.wrap( //
      new SampledTransitionRegionQuery(R2_IMAGE_REGION_WRAP.region(), RealScalar.of(0.05)), //
      new TransitionCurvatureQuery(5.));

  public static void main(String[] args) throws Exception {
    for (Tensor controlPoints : CONTROLS) {
      LaneInterface lane = StableLanes.of( //
          controlPoints, //
          LaneRiesenfeldCurveSubdivision.of(GEODESIC_INTERFACE, DEGREE)::string, //
          LEVELS, LANE_WIDTH.multiply(RationalScalar.HALF));
      for (int rep = 0; rep < REPS; rep++) {
        System.out.println("iteration " + (rep + 1));
        run(lane);
      }
    }
  }

  private static void run(LaneInterface lane) throws Exception {
    StateTime stateTime = new StateTime(lane.midLane().get(0), RealScalar.ZERO);
    SimulationEntity entity = //
        new SimulationEntity(stateTime, TRANSITION_REGION_QUERY, Tensors.vector(0, 0), R2_IMAGE_REGION_WRAP.range(), true, DELAY_HINT);
    LaneConsumer laneConsumer = new SimpleLaneConsumer(entity, null, Collections.singleton(entity));
    laneConsumer.accept(lane);
    Thread.sleep((long) (DELAY_HINT.add(OVERHEAD).number().doubleValue() * 1000));
  }
}

/** variant of {@link ClothoidLaneRrtsEntity} */
class SimulationEntity extends AbstractRrtsEntity {
  private static final StateSpaceModel STATE_SPACE_MODEL = Se2StateSpaceModel.INSTANCE;
  private final Scalar delayHint;

  /** @param stateTime initial position of entity */
  /* package */ SimulationEntity(StateTime stateTime, TransitionRegionQuery transitionRegionQuery, Tensor lbounds, Tensor ubounds, boolean greedy, Scalar delayHint) {
    super( //
        new LaneRrtsPlannerServer( //
            ClothoidTransitionSpace.INSTANCE, //
            transitionRegionQuery, //
            RationalScalar.of(1, 10), //
            STATE_SPACE_MODEL, //
            greedy) {
          private final Tensor lbounds_ = lbounds.copy().append(RealScalar.ZERO).unmodifiable();
          private final Tensor ubounds_ = ubounds.copy().append(Pi.TWO).unmodifiable();

          @Override
          protected RrtsNodeCollection rrtsNodeCollection() {
            return new RrtsNodeCollections(ClothoidRrtsNdType.INSTANCE, lbounds_, ubounds_);
          }

          @Override
          protected Tensor uBetween(StateTime orig, StateTime dest) {
            return Se2RrtsFlow.uBetween(orig, dest);
          }

          @Override
          public boolean isObserving() {
            return true;
          }

          // TODO GJOEL treat observations
        }, //
        new SimpleEpisodeIntegrator( //
            STATE_SPACE_MODEL, //
            EulerIntegrator.INSTANCE, //
            stateTime), //
        CarEntity.createPurePursuitControl());
    add(FallbackControl.of(Array.zeros(3)));
    this.delayHint = delayHint;
  }

  @Override // from AbstractRrtsEntity
  protected Tensor shape() {
    return null;
  }

  @Override // from TrajectoryEntity
  public Scalar delayHint() {
    return delayHint;
  }
}
