// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.ani.adapter.FallbackControl;
import ch.ethz.idsc.owl.ani.api.AbstractRrtsEntity;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.bot.se2.glc.CarEntity;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.rrts.LaneRrtsPlannerServer;
import ch.ethz.idsc.owl.rrts.RrtsFlowHelper;
import ch.ethz.idsc.owl.rrts.RrtsNodeCollections;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.Pi;

/* package */ class ClothoidLaneRrtsEntity extends AbstractRrtsEntity {
  private static final Scalar DELAY_HINT = RealScalar.of(3);
  private static final StateSpaceModel STATE_SPACE_MODEL = Se2StateSpaceModel.INSTANCE;
  static final Tensor SHAPE = Tensors.matrixDouble( //
      new double[][] { //
          { .2, +.07 }, //
          { .25, +.0 }, //
          { .2, -.07 }, //
          { -.1, -.07 }, //
          { -.1, +.07 } //
      }).unmodifiable();

  // ---
  /** @param stateTime initial position of entity */
  public ClothoidLaneRrtsEntity(StateTime stateTime, TransitionRegionQuery transitionRegionQuery, Tensor lbounds, Tensor ubounds) {
    super( //
        new LaneRrtsPlannerServer( //
            ClothoidTransitionSpace.INSTANCE, //
            transitionRegionQuery, //
            RationalScalar.of(1, 10), //
            STATE_SPACE_MODEL) {
          private final Tensor lbounds_ = lbounds.copy().append(RealScalar.ZERO).unmodifiable();
          private final Tensor ubounds_ = ubounds.copy().append(Pi.TWO).unmodifiable();

          @Override
          protected RrtsNodeCollection rrtsNodeCollection() {
            return RrtsNodeCollections.clothoid(lbounds_, ubounds_);
          }

          @Override
          protected Tensor uBetween(StateTime orig, StateTime dest) {
            return RrtsFlowHelper.U_SE2.apply(orig, dest);
          }
        }, //
        new SimpleEpisodeIntegrator( //
            STATE_SPACE_MODEL, //
            EulerIntegrator.INSTANCE, //
            stateTime), //
        CarEntity.createPurePursuitControl());
    add(FallbackControl.of(Array.zeros(3)));
  }

  @Override // from AbstractRrtsEntity
  protected Tensor shape() {
    return SHAPE;
  }

  @Override // from TrajectoryEntity
  public Scalar delayHint() {
    return DELAY_HINT;
  }
}
