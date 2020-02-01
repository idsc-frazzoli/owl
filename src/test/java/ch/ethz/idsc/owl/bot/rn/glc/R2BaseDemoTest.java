// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

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
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.owl.math.region.HyperplaneRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import junit.framework.TestCase;

public class R2BaseDemoTest extends TestCase {
  private static final StateSpaceModel SINGLE_INTEGRATOR = SingleIntegratorStateSpaceModel.INSTANCE;
  // static final StateIntegrator STATE_INTEGRATOR = //
  // FixedStateIntegrator.create(EulerIntegrator.INSTANCE, RealScalar.ONE, 1);

  private static TrajectoryPlanner create() {
    final Tensor stateRoot = Tensors.vector(0, 0);
    // ---
    Tensor eta = Tensors.vector(1, 1);
    Collection<Tensor> controls = new ArrayList<>();
    {
      controls.add(Tensors.vector(1, 0));
      controls.add(Tensors.vector(0, 1));
    }
    Scalar ZERO = VectorScalar.of(Array.zeros(2));
    Region<Tensor> region = HyperplaneRegion.normalize(Tensors.vector(-1, -1), RealScalar.of(10));
    TrajectoryRegionQuery trajectoryRegionQuery = SimpleTrajectoryRegionQuery.timeInvariant(region);
    GoalInterface goalInterface = new GoalAdapter(trajectoryRegionQuery, new CostFunction() {
      @Override
      public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
        return VectorScalar.of(flow);
      }

      @Override
      public Scalar minCostToGoal(Tensor x) {
        return ZERO;
      }
    });
    // ---
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        EtaRaster.state(eta), //
        FixedStateIntegrator.create(EulerIntegrator.INSTANCE, SINGLE_INTEGRATOR, RealScalar.ONE, 1), //
        controls, //
        EmptyObstacleConstraint.INSTANCE, //
        goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    return trajectoryPlanner;
  }

  public void testSimple() {
    TrajectoryPlanner trajectoryPlanner = create();
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(200);
    assertEquals(glcExpand.getExpandCount(), 15);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    assertTrue(optional.isPresent());
    List<StateTime> path = GlcNodes.getPathFromRootTo(optional.get());
    assertEquals(path.size(), 16);
    // path.stream().map(StateTime::toInfoString).forEach(System.out::println);
  }
}
