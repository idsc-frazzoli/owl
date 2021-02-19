// code by jph, gjoel
package ch.ethz.idsc.owl.bot.rn.rrts;

import ch.ethz.idsc.owl.ani.adapter.FallbackControl;
import ch.ethz.idsc.owl.ani.api.AbstractRrtsEntity;
import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.bot.rn.glc.R2TrajectoryControl;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.rrts.DefaultRrtsPlannerServer;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.sophus.math.sample.BoxRandomSample;
import ch.ethz.idsc.sophus.math.sample.ConstantRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;

// LONGTERM the redundancy in R2****Entity shows that re-factoring is needed!
/* package */ class R2RrtsEntity extends AbstractRrtsEntity {
  /** preserve 0.5[s] of the former trajectory */
  private static final Scalar DELAY_HINT = RealScalar.of(0.5);
  private static final StateSpaceModel STATE_SPACE_MODEL = SingleIntegratorStateSpaceModel.INSTANCE;
  static final Tensor SHAPE = Tensors.fromString("{{0, 0.1}, {0.1, 0}, {0, -0.1}, {-0.1, 0}}").unmodifiable();

  /** @param stateTime initial position of entity
   * @param transitionRegionQuery
   * @param lbounds
   * @param ubounds */
  public R2RrtsEntity(StateTime stateTime, TransitionRegionQuery transitionRegionQuery, Tensor lbounds, Tensor ubounds) {
    super( //
        new SimpleEpisodeIntegrator( //
            STATE_SPACE_MODEL, //
            EulerIntegrator.INSTANCE, //
            stateTime), //
        new R2TrajectoryControl(), //
        new DefaultRrtsPlannerServer( //
            RnTransitionSpace.INSTANCE, //
            transitionRegionQuery, //
            RationalScalar.of(1, 10), //
            STATE_SPACE_MODEL, //
            LengthCostFunction.INSTANCE) {
          @Override
          protected RrtsNodeCollection rrtsNodeCollection() {
            return new RnRrtsNodeCollection(lbounds, ubounds);
          }

          @Override
          protected RandomSampleInterface spaceSampler(Tensor state) {
            return BoxRandomSample.of(lbounds, ubounds);
          }

          @Override
          protected RandomSampleInterface goalSampler(Tensor goal) {
            return new ConstantRandomSample(Extract2D.FUNCTION.apply(goal));
          }

          @Override
          protected Tensor uBetween(StateTime orig, StateTime dest) {
            return RnRrtsFlow.uBetween(orig, dest);
          }
        });
    add(FallbackControl.of(Array.zeros(2)));
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
