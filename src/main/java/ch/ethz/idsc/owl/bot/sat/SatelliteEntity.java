// code by jph
package ch.ethz.idsc.owl.bot.sat;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;

import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.AbstractCircularEntity;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.state.FallbackControl;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.red.Norm2Squared;

/* package */ class SatelliteEntity extends AbstractCircularEntity {
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
    add(new FallbackControl(Array.zeros(2)));
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
  public TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    Tensor partitionScale = Tensors.vector(5, 5, 6, 6);
    StateIntegrator stateIntegrator = //
        FixedStateIntegrator.create(INTEGRATOR, RationalScalar.of(1, 12), 4);
    Tensor center = Join.of(goal.extract(0, 2), Array.zeros(2));
    GoalInterface goalInterface = SatelliteGoalManager.create( //
        center, Tensors.vector(0.5, 0.5, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    return new StandardTrajectoryPlanner( //
        partitionScale, stateIntegrator, controls, plannerConstraint, goalInterface);
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
