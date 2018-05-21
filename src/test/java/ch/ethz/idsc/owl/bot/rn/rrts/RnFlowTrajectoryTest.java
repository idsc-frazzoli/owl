// code by jph
package ch.ethz.idsc.owl.bot.rn.rrts;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.rn.RnNodeCollection;
import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TemporalTrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.math.state.TrajectoryWrap;
import ch.ethz.idsc.owl.rrts.adapter.EmptyTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
import ch.ethz.idsc.owl.rrts.core.Rrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RnFlowTrajectoryTest extends TestCase {
  public void testSimple() {
    RnTransitionSpace rnts = new RnTransitionSpace();
    RrtsNodeCollection nc = new RnNodeCollection(Tensors.vector(0, 0), Tensors.vector(10, 10));
    TransitionRegionQuery trq = EmptyTransitionRegionQuery.INSTANCE;
    Rrts rrts = new DefaultRrts(rnts, nc, trq, LengthCostFunction.IDENTITY);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 0).get();
    assertEquals(root.children().size(), 0);
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1.1, 0), 0).get();
    assertEquals(root.children().size(), 1);
    // ---
    List<RrtsNode> sequence = Arrays.asList(root, n1);
    Scalar t0 = RealScalar.ZERO;
    List<TrajectorySample> trajectory = RnFlowTrajectory.createTrajectory(rnts, sequence, t0, RealScalar.of(.2));
    TrajectoryWrap trajectorySampleMap = TrajectoryWrap.of(trajectory);
    assertFalse(trajectorySampleMap.findControl(RealScalar.of(-0.1)).isPresent());
    assertTrue(trajectorySampleMap.findControl(RealScalar.of(0.0)).isPresent());
    assertFalse(trajectorySampleMap.findControl(RealScalar.of(10.1)).isPresent());
    assertEquals(trajectorySampleMap.findControl(RealScalar.of(0.0)).get(), Tensors.vector(1, 0));
    assertTrue(trajectorySampleMap.hasRemaining(RealScalar.ZERO));
    assertFalse(trajectorySampleMap.hasRemaining(RealScalar.of(10)));
  }

  public void testDual() {
    RnTransitionSpace rnts = new RnTransitionSpace();
    RrtsNodeCollection nc = new RnNodeCollection(Tensors.vector(0, 0), Tensors.vector(10, 10));
    TransitionRegionQuery trq = EmptyTransitionRegionQuery.INSTANCE;
    Rrts rrts = new DefaultRrts(rnts, nc, trq, LengthCostFunction.IDENTITY);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 0).get();
    assertEquals(root.children().size(), 0);
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1.05, 0), 0).get();
    assertEquals(root.children().size(), 1);
    RrtsNode n2 = rrts.insertAsNode(Tensors.vector(2, 1), 0).get();
    // ---
    List<RrtsNode> sequence = Nodes.listFromRoot(n2);
    assertEquals(sequence, Arrays.asList(root, n1, n2));
    Scalar t0 = RealScalar.ZERO;
    List<TrajectorySample> trajectory = RnFlowTrajectory.createTrajectory(rnts, sequence, t0, RealScalar.of(.2));
    assertEquals(trajectory.size(), 13);
    TrajectoryWrap trajectorySampleMap = TrajectoryWrap.of(trajectory);
    assertTrue(trajectorySampleMap.hasRemaining(RealScalar.ZERO));
    assertFalse(trajectorySampleMap.hasRemaining(RealScalar.of(10)));
    {
      Optional<Tensor> optional = trajectorySampleMap.findControl(RealScalar.of(1.5));
      assertTrue(optional.isPresent());
    }
    // Trajectories.print(trajectory);
    {
      TrajectoryControl tc = TemporalTrajectoryControl.createInstance();
      tc.setTrajectory(null);
      tc.setTrajectory(trajectory);
      Tensor u = tc.control(new StateTime(Tensors.vector(1, 2), RealScalar.of(1.2)), RealScalar.of(2)).get();
      assertTrue(Chop._10.close(Norm._2.of(u), RealScalar.ONE));
      List<TrajectorySample> list = tc.getFutureTrajectoryUntil(new StateTime(Tensors.vector(1, 2), RealScalar.of(1.1)), RealScalar.of(1));
      assertEquals(list.size(), 11);
    }
  }

  public void testBetween() {
    StateTime orig = new StateTime(Tensors.fromString("{4[m],5[m]}"), Quantity.of(3, "s"));
    StateTime dest = new StateTime(Tensors.fromString("{10[m],13[m]}"), Quantity.of(5, "s"));
    Flow flow = RnFlowTrajectory.between(orig, dest);
    Tensor u = flow.getU();
    assertEquals(u, Tensors.fromString("{3[m*s^-1], 4[m*s^-1]}"));
  }
}
