// code by gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransitionSpace;
import ch.ethz.idsc.owl.bot.se2.rrts.DubinsTransitionSpace;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.adapter.DirectionalTransitionSpace;
import ch.ethz.idsc.owl.rrts.adapter.EmptyTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
import ch.ethz.idsc.owl.rrts.core.Rrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RrtsFlowTrajectoryGeneratorTest extends TestCase {
  public void testRn() {
    Rrts rrts = new DefaultRrts( //
        RnTransitionSpace.INSTANCE, //
        RrtsNodeCollections.rn(Tensors.vector(0, 0), Tensors.vector(10, 10)), //
        EmptyTransitionRegionQuery.INSTANCE, LengthCostFunction.IDENTITY);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 0).get();
    assertEquals(0, root.children().size());
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1, 0), 0).get();
    assertEquals(1, root.children().size());
    /* RrtsNode n_1 = */ rrts.insertAsNode(Tensors.vector(-1, 0), 0).get();
    assertEquals(2, root.children().size());
    RrtsNode n2 = rrts.insertAsNode(Tensors.vector(2, 0), 0).get();
    assertEquals(1, n1.children().size());
    RrtsNode n3 = rrts.insertAsNode(Tensors.vector(3, 0), 0).get();
    assertEquals(1, n2.children().size());
    // ---
    List<RrtsNode> sequence = Nodes.listFromRoot(n3);
    assertEquals(sequence, Arrays.asList(root, n1, n2, n3));
    RrtsFlowTrajectoryGenerator generator = new RrtsFlowTrajectoryGenerator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        RrtsFlowHelper.U_R2);
    List<TrajectorySample> trajectory = //
        generator.createTrajectory(RnTransitionSpace.INSTANCE, sequence, RealScalar.ZERO, RationalScalar.of(1, 10));
    assertEquals(31, trajectory.size());
    for (int i = 1; i < 31; i++) {
      TrajectorySample sample = trajectory.get(i);
      assertEquals(RationalScalar.of(i, 10), sample.stateTime().time());
      assertEquals(Tensors.of(sample.stateTime().time(), RealScalar.ZERO), sample.stateTime().state());
      assertEquals(Tensors.vector(1, 0), sample.getFlow().get().getU());
    }
    assertFalse(trajectory.get(0).getFlow().isPresent());
    assertTrue(trajectory.subList(1, 31).stream().map(TrajectorySample::getFlow).allMatch(Optional::isPresent));
    Chop._15.requireClose(root.state(), trajectory.get(0).stateTime().state());
    Chop._15.requireClose(n1.state(), trajectory.get(10).stateTime().state());
    Chop._15.requireClose(n2.state(), trajectory.get(20).stateTime().state());
    Chop._15.requireClose(n3.state(), Lists.getLast(trajectory).stateTime().state());
  }

  public void testDubins() {
    Rrts rrts = new DefaultRrts( //
        DubinsTransitionSpace.of(RealScalar.ONE), //
        RrtsNodeCollections.euclidean(Tensors.vector(0, 0, 0), Tensors.vector(10, 10, 2 * Math.PI)), //
        EmptyTransitionRegionQuery.INSTANCE, LengthCostFunction.IDENTITY);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0, 0), 0).get();
    assertEquals(0, root.children().size());
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1, 0, 0), 0).get();
    assertEquals(1, root.children().size());
    /* RrtsNode n_1 = */ rrts.insertAsNode(Tensors.vector(-1, 0, 0), 0).get();
    assertEquals(2, root.children().size());
    RrtsNode n2 = rrts.insertAsNode(Tensors.vector(2, 0, 0), 0).get();
    assertEquals(1, n1.children().size());
    RrtsNode n3 = rrts.insertAsNode(Tensors.vector(3, 1, Math.PI / 2), 0).get();
    assertEquals(1, n2.children().size());
    // ---
    List<RrtsNode> sequence = Nodes.listFromRoot(n3);
    assertEquals(sequence, Arrays.asList(root, n1, n2, n3));
    RrtsFlowTrajectoryGenerator generator = new RrtsFlowTrajectoryGenerator( //
        Se2StateSpaceModel.INSTANCE, //
        RrtsFlowHelper.U_R2);
    List<TrajectorySample> trajectory = //
        generator.createTrajectory(DubinsTransitionSpace.of(RealScalar.ONE), sequence, RealScalar.ZERO, RationalScalar.of(1, 10));
    // trajectory.stream().map(TrajectorySample::toInfoString).forEach(System.out::println);
    assertEquals(37, trajectory.size());
    for (int i = 1; i < 21; i++) {
      TrajectorySample sample = trajectory.get(i);
      assertTrue(Chop._15.close(sample.stateTime().time(), RationalScalar.of(i, 10)));
      assertTrue(Chop._15.close(sample.stateTime().state(), Tensors.of(sample.stateTime().time(), RealScalar.ZERO, RealScalar.ZERO)));
      assertTrue(Chop._14.close(sample.getFlow().get().getU(), Tensors.vector(1, 0, 0)));
    }
    assertFalse(trajectory.get(0).getFlow().isPresent());
    assertTrue(trajectory.subList(1, 37).stream().map(TrajectorySample::getFlow).allMatch(Optional::isPresent));
    // TODO verify correctness of U
    Chop._15.requireClose(root.state(), trajectory.get(0).stateTime().state());
    Chop._15.requireClose(n1.state(), trajectory.get(10).stateTime().state());
    Chop._15.requireClose(n2.state(), trajectory.get(20).stateTime().state());
    Chop._15.requireClose(n3.state(), Lists.getLast(trajectory).stateTime().state());
  }

  public void testClothoid() {
    Rrts rrts = new DefaultRrts( //
        ClothoidTransitionSpace.INSTANCE, //
        RrtsNodeCollections.clothoid(Tensors.vector(0, 0, 0), Tensors.vector(10, 10, 2 * Math.PI)), //
        EmptyTransitionRegionQuery.INSTANCE, LengthCostFunction.IDENTITY);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0, 0), 0).get();
    assertEquals(0, root.children().size());
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1, 0, 0), 0).get();
    assertEquals(1, root.children().size());
    /* RrtsNode n_1 = */ rrts.insertAsNode(Tensors.vector(-1, 0, 0), 0).get();
    assertEquals(2, root.children().size());
    RrtsNode n2 = rrts.insertAsNode(Tensors.vector(2, 0, 0), 0).get();
    assertEquals(1, n1.children().size());
    RrtsNode n3 = rrts.insertAsNode(Tensors.vector(3, 2, Math.PI / 2), 0).get();
    assertEquals(1, n2.children().size());
    // ---
    List<RrtsNode> sequence = Nodes.listFromRoot(n3);
    assertEquals(sequence, Arrays.asList(root, n1, n2, n3));
    RrtsFlowTrajectoryGenerator generator = new RrtsFlowTrajectoryGenerator( //
        Se2StateSpaceModel.INSTANCE, //
        RrtsFlowHelper.U_SE2);
    List<TrajectorySample> trajectory = //
        generator.createTrajectory(ClothoidTransitionSpace.INSTANCE, sequence, RealScalar.ZERO, RationalScalar.of(1, 10));
    // trajectory.stream().map(TrajectorySample::toInfoString).forEach(System.out::println);
    assertEquals(65, trajectory.size());
    for (int i = 1; i < 33; i++) {
      TrajectorySample sample = trajectory.get(i);
      assertEquals(RationalScalar.of(i, 16), sample.stateTime().time());
      assertEquals(Tensors.of(sample.stateTime().time(), RealScalar.ZERO, RealScalar.ZERO), sample.stateTime().state());
      assertEquals(Tensors.vector(1, 0, 0), sample.getFlow().get().getU());
    }
    assertFalse(trajectory.get(0).getFlow().isPresent());
    assertTrue(trajectory.subList(1, 65).stream().map(TrajectorySample::getFlow).map(Optional::get).map(Flow::getU) //
        .allMatch(u -> u.Get(1).equals(RealScalar.ZERO)));
    // TODO verify correctness of U
    Chop._15.requireClose(root.state(), trajectory.get(0).stateTime().state());
    Chop._15.requireClose(n1.state(), trajectory.get(16).stateTime().state());
    Chop._15.requireClose(n2.state(), trajectory.get(32).stateTime().state());
    Chop._15.requireClose(n3.state(), Lists.getLast(trajectory).stateTime().state());
  }

  public void testDirectionalClothoid() {
    Rrts rrts = new DefaultRrts( //
        DirectionalTransitionSpace.of(ClothoidTransitionSpace.INSTANCE), //
        // no specific collection for directional clothoid
        RrtsNodeCollections.euclidean(Tensors.vector(0, 0, 0), Tensors.vector(10, 10, 2 * Math.PI)), //
        EmptyTransitionRegionQuery.INSTANCE, LengthCostFunction.IDENTITY);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0, 0), 0).get();
    assertEquals(0, root.children().size());
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1, 0, 0), 0).get();
    assertEquals(1, root.children().size());
    /* RrtsNode n_1 = */ rrts.insertAsNode(Tensors.vector(-1, 0, 0), 0).get();
    assertEquals(2, root.children().size());
    RrtsNode n2 = rrts.insertAsNode(Tensors.vector(2, 1, Math.PI / 2), 0).get();
    assertEquals(1, n1.children().size());
    RrtsNode n3 = rrts.insertAsNode(Tensors.vector(3, 0, Math.PI), 0).get();
    assertEquals(1, n2.children().size());
    // ---
    List<RrtsNode> sequence = Nodes.listFromRoot(n3);
    assertEquals(sequence, Arrays.asList(root, n1, n2, n3));
    RrtsFlowTrajectoryGenerator generator = new RrtsFlowTrajectoryGenerator( //
        Se2StateSpaceModel.INSTANCE, //
        RrtsFlowHelper.U_SE2);
    List<TrajectorySample> trajectory = //
        generator.createTrajectory(DirectionalTransitionSpace.of(ClothoidTransitionSpace.INSTANCE), sequence, RealScalar.ZERO, RationalScalar.of(1, 10));
    // trajectory.stream().map(TrajectorySample::toInfoString).forEach(System.out::println);
    assertEquals(49, trajectory.size());
    for (int i = 1; i < 17; i++) {
      TrajectorySample sample = trajectory.get(i);
      assertEquals(RationalScalar.of(i, 16), sample.stateTime().time());
      assertEquals(Tensors.of(sample.stateTime().time(), RealScalar.ZERO, RealScalar.ZERO), sample.stateTime().state());
      assertTrue(Chop._15.close(sample.getFlow().get().getU(), Tensors.vector(1, 0, 0)));
    }
    assertFalse(trajectory.get(0).getFlow().isPresent());
    assertTrue(trajectory.subList(1, 49).stream().map(TrajectorySample::getFlow).allMatch(Optional::isPresent));
    // TODO verify correctness of U
    Chop._15.requireClose(root.state(), trajectory.get(0).stateTime().state());
    Chop._15.requireClose(n1.state(), trajectory.get(16).stateTime().state());
    Chop._15.requireClose(n2.state(), trajectory.get(32).stateTime().state());
    Chop._15.requireClose(n3.state(), Lists.getLast(trajectory).stateTime().state());
  }
}
