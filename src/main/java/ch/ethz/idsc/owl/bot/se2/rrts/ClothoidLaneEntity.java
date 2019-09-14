// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.util.Map;
import java.util.function.Consumer;

import ch.ethz.idsc.owl.ani.adapter.FallbackControl;
import ch.ethz.idsc.owl.ani.api.AbstractRrtsEntity;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.bot.se2.glc.CarEntity;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

/** variant of {@link ClothoidLaneRrtsEntity} intended for simulation
 * TODO GJOEL perhaps create intermediate class with common code to derive from */
/* package */ class ClothoidLaneEntity extends AbstractRrtsEntity {
  private static final StateSpaceModel STATE_SPACE_MODEL = Se2StateSpaceModel.INSTANCE;
  private final Scalar delayHint;

  /** @param stateTime initial position of entity */
  /* package */ ClothoidLaneEntity(StateTime stateTime, TransitionRegionQuery transitionRegionQuery, Tensor lbounds, Tensor ubounds, boolean greedy,
      Scalar delayHint, Consumer<Map<Double, Scalar>> process, Consumer<RrtsNode> processFirst, Consumer<RrtsNode> processLast) {
    super( //
        new SimpleEpisodeIntegrator( //
            STATE_SPACE_MODEL, //
            EulerIntegrator.INSTANCE, //
            stateTime), //
        CarEntity.createPurePursuitControl(), //
        new LaneRrtsPlannerServer( //
            ClothoidTransitionSpace.INSTANCE, //
            transitionRegionQuery, //
            RationalScalar.of(1, 10), //
            STATE_SPACE_MODEL, //
            LengthCostFunction.INSTANCE, //
            greedy) {
          @Override // from DefaultRrtsPlannerServer
          protected RrtsNodeCollection rrtsNodeCollection() {
            return Se2TransitionRrtsNodeCollections.of(getTransitionSpace(), lbounds, ubounds);
          }

          @Override // from RrtsPlannerServer
          protected Tensor uBetween(StateTime orig, StateTime dest) {
            return Se2RrtsFlow.uBetween(orig, dest);
          }

          @Override // from ObservingExpandInterface
          public boolean isObserving() {
            return true;
          }

          @Override // from ObservingExpandInterface
          public void process(Map<Double, Scalar> observations) {
            process.accept(observations);
            super.process(observations);
          }

          @Override // from ObservingExpandInterface
          public void processFirst(RrtsNode first) {
            processFirst.accept(first);
          }

          @Override // from ObservingExpandInterface
          public void processLast(RrtsNode last) {
            processLast.accept(last);
          }
        });
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