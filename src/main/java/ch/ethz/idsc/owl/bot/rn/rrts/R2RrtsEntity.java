// code by jph
package ch.ethz.idsc.owl.bot.rn.rrts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import ch.ethz.idsc.owl.ani.adapter.FallbackControl;
import ch.ethz.idsc.owl.ani.api.AbstractRrtsEntity;
import ch.ethz.idsc.owl.ani.api.RrtsPlannerCallback;
import ch.ethz.idsc.owl.bot.rn.glc.R2TrajectoryControl;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.adapter.EmptyTransitionRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.red.Norm2Squared;

// LONGTERM the redundancy in R2****Entity shows that re-factoring is needed!
/* package */ class R2RrtsEntity extends AbstractRrtsEntity {
  /** preserve 0.5[s] of the former trajectory */
  private static final Scalar DELAY_HINT = RealScalar.of(0.5);

  // ---
  /** @param state initial position of entity */
  public R2RrtsEntity(Tensor state) {
    super(new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(state, RealScalar.ZERO)), //
        new R2TrajectoryControl());
    add(FallbackControl.of(Array.zeros(2)));
  }

  @Override // from TensorMetrix
  public Scalar distance(Tensor x, Tensor y) {
    return Norm2Squared.between(x, y); // non-negative
  }

  @Override
  public Scalar delayHint() {
    return DELAY_HINT;
  }

  @Override
  public final TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    throw new RuntimeException(); // LONGTERM API not finalized
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    { // indicate current position
      Tensor state = getStateTimeNow().state();
      Point2D point = geometricLayer.toPoint2D(state);
      graphics.setColor(new Color(64, 128, 64, 192));
      graphics.fill(new Ellipse2D.Double(point.getX() - 2, point.getY() - 2, 7, 7));
    }
    { // indicate position 1[s] into the future
      Tensor state = getEstimatedLocationAt(DELAY_HINT);
      Point2D point = geometricLayer.toPoint2D(state);
      graphics.setColor(new Color(255, 128, 128 - 64, 128 + 64));
      graphics.fill(new Rectangle2D.Double(point.getX() - 2, point.getY() - 2, 5, 5));
    }
  }

  @Override
  public void startPlanner( //
      RrtsPlannerCallback rrtsPlannerCallback, List<TrajectorySample> head, Tensor goal) {
    StateTime tail = Lists.getLast(head).stateTime();
    NoiseCircleHelper noiseCircleHelper = //
        new NoiseCircleHelper(EmptyTransitionRegionQuery.INSTANCE, tail, Extract2D.FUNCTION.apply(goal));
    noiseCircleHelper.plan(350);
    if (noiseCircleHelper.trajectory != null) {
      System.out.println("found!");
      rrtsPlannerCallback.expandResult(head, noiseCircleHelper.getRrtsPlanner(), noiseCircleHelper.trajectory);
    }
  }
}
