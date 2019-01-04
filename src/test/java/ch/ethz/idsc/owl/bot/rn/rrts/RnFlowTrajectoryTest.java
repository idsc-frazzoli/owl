// code by jph
package ch.ethz.idsc.owl.bot.rn.rrts;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.owl.bot.rn.RnRrtsNodeCollection;
import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.math.state.TrajectoryWrap;
import ch.ethz.idsc.owl.rrts.adapter.EmptyTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
import ch.ethz.idsc.owl.rrts.core.Rrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class RnFlowTrajectoryTest extends TestCase {
  private static final TransitionSpace TRANSITION_SPACE = RnTransitionSpace.INSTANCE;

  public void testSimple() {
    RrtsNodeCollection nc = new RnRrtsNodeCollection(Tensors.vector(0, 0), Tensors.vector(10, 10));
    TransitionRegionQuery trq = EmptyTransitionRegionQuery.INSTANCE;
    Rrts rrts = new DefaultRrts(TRANSITION_SPACE, nc, trq, LengthCostFunction.IDENTITY);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 0).get();
    assertEquals(root.children().size(), 0);
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1.1, 0), 0).get();
    assertEquals(root.children().size(), 1);
    // ---
    List<RrtsNode> sequence = Arrays.asList(root, n1);
    Scalar t0 = RealScalar.ZERO;
    List<TrajectorySample> trajectory = RnFlowTrajectory.createTrajectory(TRANSITION_SPACE, sequence, t0, RealScalar.of(.2));
    TrajectoryWrap trajectorySampleMap = TrajectoryWrap.of(trajectory);
    // assertFalse(trajectorySampleMap.findControl(RealScalar.of(-0.1)).isPresent());
    // trajectorySampleMap.getControl(RealScalar.of(0.0));
    // // assertTrue();
    // // assertFalse(trajectorySampleMap.findControl(RealScalar.of(10.1)).isPresent());
    // assertEquals(trajectorySampleMap.getControl(RealScalar.of(0.0)), Tensors.vector(1, 0));
    // assertTrue(trajectorySampleMap.isRelevant(RealScalar.of(-1000)));
    // assertFalse(trajectorySampleMap.isRelevant(RealScalar.of(1000)));
    // assertTrue(trajectorySampleMap.isDefined(RealScalar.ZERO));
    // assertFalse(trajectorySampleMap.isDefined(RealScalar.of(-1000)));
    // assertFalse(trajectorySampleMap.isDefined(RealScalar.of(10)));
  }

  public void testDual() {
    RrtsNodeCollection nc = new RnRrtsNodeCollection(Tensors.vector(0, 0), Tensors.vector(10, 10));
    TransitionRegionQuery trq = EmptyTransitionRegionQuery.INSTANCE;
    Rrts rrts = new DefaultRrts(TRANSITION_SPACE, nc, trq, LengthCostFunction.IDENTITY);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 0).get();
    assertEquals(root.children().size(), 0);
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1.05, 0), 0).get();
    assertEquals(root.children().size(), 1);
    RrtsNode n2 = rrts.insertAsNode(Tensors.vector(2, 1), 0).get();
    // ---
    List<RrtsNode> sequence = Nodes.listFromRoot(n2);
    assertEquals(sequence, Arrays.asList(root, n1, n2));
    Scalar t0 = RealScalar.ZERO;
    List<TrajectorySample> trajectory = RnFlowTrajectory.createTrajectory(TRANSITION_SPACE, sequence, t0, RealScalar.of(.2));
    // assertEquals(trajectory.size(), 13);
    // TrajectoryWrap trajectorySampleMap = TrajectoryWrap.of(trajectory);
    // assertTrue(trajectorySampleMap.isDefined(RealScalar.ZERO));
    // assertFalse(trajectorySampleMap.isDefined(RealScalar.of(10)));
    // {
    // Tensor vector = trajectorySampleMap.getControl(RealScalar.of(1.5));
    // assertTrue(Chop._14.close(Norm._2.ofVector(vector), RealScalar.ONE));
    // }
    // // Trajectories.print(trajectory);
    // {
    // TrajectoryControl tc = TemporalTrajectoryControl.createInstance();
    // tc.trajectory(null);
    // tc.trajectory(trajectory);
    // Tensor u = tc.control(new StateTime(Tensors.vector(1, 2), RealScalar.of(1.2)), RealScalar.of(2)).get();
    // assertTrue(Chop._10.close(Norm._2.of(u), RealScalar.ONE));
    // List<TrajectorySample> list = tc.getFutureTrajectoryUntil(new StateTime(Tensors.vector(1, 2), RealScalar.of(1.1)), RealScalar.of(1));
    // assertEquals(list.size(), 11);
    // }
  }

  public void testBetween() {
    StateTime orig = new StateTime(Tensors.fromString("{4[m],5[m]}"), Quantity.of(3, "s"));
    StateTime dest = new StateTime(Tensors.fromString("{10[m],13[m]}"), Quantity.of(5, "s"));
    Flow flow = RnFlowTrajectory.between(orig, dest);
    Tensor u = flow.getU();
    assertEquals(u, Tensors.fromString("{3[m*s^-1], 4[m*s^-1]}"));
  }
}
