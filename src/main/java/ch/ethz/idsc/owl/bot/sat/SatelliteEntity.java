// code by jph
package ch.ethz.idsc.owl.bot.sat;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;

import ch.ethz.idsc.owl.ani.adapter.FallbackControl;
import ch.ethz.idsc.owl.ani.api.AbstractCircularEntity;
import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.GlcTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardGlcTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.lie.se2.Se2Utils;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.red.Norm2Squared;

/* package */ class SatelliteEntity extends AbstractCircularEntity {
  protected static final Tensor PARTITION_SCALE = Tensors.vector(5, 5, 6, 6).unmodifiable();
  private static final Tensor SHAPE = Tensors.matrixDouble( //
      new double[][] { { .3, 0, 1 }, { -.1, -.1, 1 }, { -.1, +.1, 1 } }).unmodifiable();
  private static final SatelliteStateSpaceModel SATELLITE_MODEL = new SatelliteStateSpaceModel();
  private static final Integrator INTEGRATOR = RungeKutta45Integrator.INSTANCE;
  // ---
  private final Collection<Flow> controls;
  public Scalar delayHint = RealScalar.ONE;

  /** @param state initial position of entity */
  public SatelliteEntity(Tensor state, TrajectoryControl trajectoryControl, Collection<Flow> controls) {
    super( //
        new SimpleEpisodeIntegrator(SATELLITE_MODEL, INTEGRATOR, new StateTime(state, RealScalar.ZERO)), //
        trajectoryControl);
    add(FallbackControl.of(Array.zeros(2)));
    this.controls = controls;
  }

  @Override
  public Scalar distance(Tensor x, Tensor y) {
    return Norm2Squared.between(x, y); // non-negative
  }

  @Override
  public final Scalar delayHint() {
    return delayHint;
  }

  @Override
  public final GlcTrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    StateIntegrator stateIntegrator = //
        FixedStateIntegrator.create(INTEGRATOR, RationalScalar.of(1, 12), 4);
    Tensor center = Join.of(Extract2D.FUNCTION.apply(goal), Array.zeros(2));
    GoalInterface goalInterface = SatelliteGoalManager.create( //
        center, Tensors.vector(0.5, 0.5, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    StateTimeRaster stateTimeRaster = EtaRaster.state(PARTITION_SCALE);
    return new StandardGlcTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, plannerConstraint, goalInterface);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    super.render(geometricLayer, graphics);
    {
      Tensor xya = geometricLayer.getMouseSe2State();
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
      graphics.setColor(new Color(0, 128, 255, 192));
      graphics.fill(geometricLayer.toPath2D(SHAPE));
      geometricLayer.popMatrix();
    }
  }
}
