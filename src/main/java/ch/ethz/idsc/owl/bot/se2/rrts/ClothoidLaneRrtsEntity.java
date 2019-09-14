// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.Graphics2D;
import java.util.Optional;

import ch.ethz.idsc.owl.ani.adapter.FallbackControl;
import ch.ethz.idsc.owl.ani.api.AbstractRrtsEntity;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.bot.se2.glc.CarEntity;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.rrts.LaneRrtsPlannerServer;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;

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
  public ClothoidLaneRrtsEntity(StateTime stateTime, TransitionRegionQuery transitionRegionQuery, Tensor lbounds, Tensor ubounds) {
    this(stateTime, transitionRegionQuery, lbounds, ubounds, false);
  }

  /** @param stateTime initial position of entity */
  public ClothoidLaneRrtsEntity(StateTime stateTime, TransitionRegionQuery transitionRegionQuery, Tensor lbounds, Tensor ubounds, boolean greedy) {
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
          @Override
          protected RrtsNodeCollection rrtsNodeCollection() {
            return ClothoidRrtsNdTypeCollections.of(lbounds, ubounds);
          }

          @Override
          protected Tensor uBetween(StateTime orig, StateTime dest) {
            return Se2RrtsFlow.uBetween(orig, dest);
          }
        });
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

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    LaneRrtsPlannerServer laneRrtsPlannerServer = (LaneRrtsPlannerServer) rrtsPlannerServer;
    Optional<Region<Tensor>> goalRegion = laneRrtsPlannerServer.goalRegion();
    if (goalRegion.isPresent())
      RegionRenders.draw(geometricLayer, graphics, goalRegion.get());
    super.render(geometricLayer, graphics);
  }

  public void setConical(boolean conical) {
    ((LaneRrtsPlannerServer) rrtsPlannerServer).setConical(conical);
  }
}
