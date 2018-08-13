// code by jph
package ch.ethz.idsc.owl.bot.rn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.EmptyObstacleConstraint;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.GoalAdapter;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.HyperplaneRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;

// TODO JAN move class to test area and use RLplanner
/* package */ enum RnLexiDemo {
  ;
  private static final StateSpaceModel SINGLE_INTEGRATOR = SingleIntegratorStateSpaceModel.INSTANCE;
  static final StateIntegrator STATE_INTEGRATOR = //
      FixedStateIntegrator.create(EulerIntegrator.INSTANCE, RationalScalar.ONE, 1);

  private static TrajectoryPlanner simple() {
    final Tensor stateRoot = Tensors.vector(0, 0);
    // ---
    Tensor eta = Tensors.vector(1, 1);
    Collection<Flow> controls = new ArrayList<>();
    {
      controls.add(StateSpaceModels.createFlow(SINGLE_INTEGRATOR, Tensors.vector(1, 0)));
      controls.add(StateSpaceModels.createFlow(SINGLE_INTEGRATOR, Tensors.vector(0, 1)));
    }
    Scalar ZERO = VectorScalar.of(Array.zeros(2));
    Region<Tensor> region = HyperplaneRegion.normalize(Tensors.vector(-1, -1), RealScalar.of(10));
    TrajectoryRegionQuery trajectoryRegionQuery = SimpleTrajectoryRegionQuery.timeInvariant(region);
    GoalInterface goalInterface = new GoalAdapter(trajectoryRegionQuery, new CostFunction() {
      @Override
      public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
        return VectorScalar.of(flow.getU());
      }

      @Override
      public Scalar minCostToGoal(Tensor x) {
        return ZERO;
      }
    });
    // ---
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        EtaRaster.state(eta), //
        STATE_INTEGRATOR, //
        controls, //
        EmptyObstacleConstraint.INSTANCE, //
        goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    return trajectoryPlanner;
  }

  public static void main(String[] args) {
    TrajectoryPlanner trajectoryPlanner = simple();
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(200);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> path = GlcNodes.getPathFromRootTo(optional.get());
      path.stream().map(StateTime::toInfoString).forEach(System.out::println);
    }
  }
}
