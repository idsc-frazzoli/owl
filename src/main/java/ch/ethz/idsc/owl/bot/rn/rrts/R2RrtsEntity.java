// code by jph, gjoel
package ch.ethz.idsc.owl.bot.rn.rrts;

import ch.ethz.idsc.owl.ani.adapter.FallbackControl;
import ch.ethz.idsc.owl.ani.api.AbstractRrtsEntity;
import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.bot.rn.glc.R2TrajectoryControl;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.sample.BoxRandomSample;
import ch.ethz.idsc.owl.math.sample.ConstantRandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.rrts.RrtsNodeCollections;
import ch.ethz.idsc.owl.rrts.RrtsPlannerServer;
import ch.ethz.idsc.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.RrtsTrajectoryPlanner;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.red.Norm2Squared;

// LONGTERM the redundancy in R2****Entity shows that re-factoring is needed!
/* package */ class R2RrtsEntity extends AbstractRrtsEntity {
  /** preserve 0.5[s] of the former trajectory */
  private static final Scalar DELAY_HINT = RealScalar.of(0.5);
  private static final StateSpaceModel STATE_SPACE_MODEL = SingleIntegratorStateSpaceModel.INSTANCE;
  private static final Tensor SHAPE = Tensors.fromString("{{0,.1},{.1,0},{0,-.1},{-.1,0}}").unmodifiable();

  // ---
  /** @param stateTime initial position of entity */
  public R2RrtsEntity(StateTime stateTime, ImageRegion imageRegion) {
    super( //
        new RrtsPlannerServer( //
            RnTransitionSpace.INSTANCE, //
            new SampledTransitionRegionQuery(imageRegion, RealScalar.of(0.05)), //
            RationalScalar.of(1, 10), //
            SingleIntegratorStateSpaceModel.INSTANCE) {
          @Override
          protected RrtsNodeCollection rrtsNodeCollection() {
            return RrtsNodeCollections.rn(imageRegion.origin(), imageRegion.range());
          }

          @Override
          protected RandomSampleInterface spaceSampler(Tensor state) {
            return BoxRandomSample.of(imageRegion.origin(), imageRegion.range());
          }

          @Override
          protected RandomSampleInterface goalSampler(Tensor goal) {
            return new ConstantRandomSample(Extract2D.FUNCTION.apply(goal));
          }
        }, //
        new SimpleEpisodeIntegrator( //
            STATE_SPACE_MODEL, //
            EulerIntegrator.INSTANCE, //
            stateTime), //
        new R2TrajectoryControl());
    add(FallbackControl.of(Array.zeros(2)));
  }

  protected Tensor shape() {
    return SHAPE;
  }

  @Override // from TensorMetrix
  public Scalar distance(Tensor x, Tensor y) {
    return Norm2Squared.between(x, y); // non-negative
  }

  @Override // from TrajectoryEntity
  public Scalar delayHint() {
    return DELAY_HINT;
  }

  @Override // from TrajectoryEntity
  public final RrtsTrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    plannerServer.setGoal(goal);
    return plannerServer;
  }
}
