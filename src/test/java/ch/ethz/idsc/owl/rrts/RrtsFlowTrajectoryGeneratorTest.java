// code by gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransitionSpace;
import ch.ethz.idsc.owl.bot.se2.rrts.DubinsTransitionSpace;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.adapter.Directional;
import ch.ethz.idsc.owl.rrts.adapter.EmptyTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
import ch.ethz.idsc.owl.rrts.core.Rrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.N;
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
    RrtsFlowTrajectoryGenerator generator = new RrtsFlowTrajectoryGenerator(SingleIntegratorStateSpaceModel.INSTANCE);
    List<TrajectorySample> trajectory = //
        generator.createTrajectory(RnTransitionSpace.INSTANCE, sequence, RealScalar.ZERO, RationalScalar.of(1, 10));
    assertEquals(30, trajectory.size());
    assertTrue(IntStream.range(1, 30).allMatch(i -> {
      TrajectorySample sample = trajectory.get(i);
      return sample.stateTime().time().equals(RationalScalar.of(i, 10)) //
          && sample.stateTime().state().equals(Tensors.of(RationalScalar.of(i, 10), RealScalar.ZERO)) //
          && sample.getFlow().get().getU().equals(Tensors.vector(1, 0));
    }));
    Chop._15.requireClose(root.state(), trajectory.get(0).stateTime().state());
    Chop._15.requireClose(n1.state(), trajectory.get(10).stateTime().state());
    Chop._15.requireClose(n2.state(), trajectory.get(20).stateTime().state());
    Chop.below(.2).requireClose(n3.state(), N.DOUBLE.of(Lists.getLast(trajectory).stateTime().state()));
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
    RrtsFlowTrajectoryGenerator generator = new RrtsFlowTrajectoryGenerator(Se2StateSpaceModel.INSTANCE /* SingleIntegratorStateSpaceModel.INSTANCE */ );
    List<TrajectorySample> trajectory = //
        generator.createTrajectory(RnTransitionSpace.INSTANCE, sequence, RealScalar.ZERO, RationalScalar.of(1, 10));
    // trajectory.stream().map(TrajectorySample::toInfoString).forEach(System.out::println);
    assertEquals(42, trajectory.size());
    assertTrue(IntStream.range(1, 20).allMatch(i -> {
      TrajectorySample sample = trajectory.get(i);
      return sample.stateTime().time().equals(RationalScalar.of(i, 10)) //
          && sample.stateTime().state().equals(Tensors.of(RationalScalar.of(i, 10), RealScalar.ZERO, RealScalar.ZERO)) //
          && sample.getFlow().get().getU().map(Chop._15).equals(Tensors.vector(1, 0, 0));
    }));
    // TODO verify correctness of U
    Chop._15.requireClose(root.state(), trajectory.get(0).stateTime().state());
    Chop._15.requireClose(n1.state(), trajectory.get(10).stateTime().state());
    Chop._15.requireClose(n2.state(), trajectory.get(20).stateTime().state());
    Chop._01.requireClose(n3.state(), N.DOUBLE.of(Lists.getLast(trajectory).stateTime().state()));
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
    RrtsFlowTrajectoryGenerator generator = new RrtsFlowTrajectoryGenerator(Se2StateSpaceModel.INSTANCE);
    List<TrajectorySample> trajectory = //
        generator.createTrajectory(RnTransitionSpace.INSTANCE, sequence, RealScalar.ZERO, RationalScalar.of(1, 10));
    // trajectory.stream().map(TrajectorySample::toInfoString).forEach(System.out::println);
    assertEquals(48, trajectory.size());
    assertTrue(IntStream.range(1, 20).allMatch(i -> {
      TrajectorySample sample = trajectory.get(i);
      return sample.stateTime().time().equals(RationalScalar.of(i, 10)) //
          && sample.stateTime().state().equals(Tensors.of(RationalScalar.of(i, 10), RealScalar.ZERO, RealScalar.ZERO)) //
          && sample.getFlow().get().getU().map(Chop._15).equals(Tensors.vector(1, 0, 0));
    }));
    // TODO verify correctness of U
    Chop._15.requireClose(root.state(), trajectory.get(0).stateTime().state());
    Chop._15.requireClose(n1.state(), trajectory.get(10).stateTime().state());
    Chop._15.requireClose(n2.state(), trajectory.get(20).stateTime().state());
    Chop._01.requireClose(n3.state(), N.DOUBLE.of(Lists.getLast(trajectory).stateTime().state()));
  }

  public void testDirectionalClothoid() {
    Rrts rrts = new DefaultRrts( //
        Directional.of(ClothoidTransitionSpace.INSTANCE), //
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
    RrtsFlowTrajectoryGenerator generator = new RrtsFlowTrajectoryGenerator(Se2StateSpaceModel.INSTANCE);
    List<TrajectorySample> trajectory = //
        generator.createTrajectory(RnTransitionSpace.INSTANCE, sequence, RealScalar.ZERO, RationalScalar.of(1, 10));
    // trajectory.stream().map(TrajectorySample::toInfoString).forEach(System.out::println);
    assertEquals(54, trajectory.size());
    assertTrue(IntStream.range(1, 10).allMatch(i -> {
      TrajectorySample sample = trajectory.get(i);
      return sample.stateTime().time().equals(RationalScalar.of(i, 10)) //
          && sample.stateTime().state().equals(Tensors.of(RationalScalar.of(i, 10), RealScalar.ZERO, RealScalar.ZERO)) //
          && sample.getFlow().get().getU().map(Chop._15).equals(Tensors.vector(1, 0, 0));
    }));
    // TODO verify correctness of U
    Chop._15.requireClose(root.state(), trajectory.get(0).stateTime().state());
    Chop._15.requireClose(n1.state(), trajectory.get(10).stateTime().state());
    Chop._15.requireClose(n2.state(), trajectory.get(32).stateTime().state());
    Chop._01.requireClose(n3.state(), N.DOUBLE.of(Lists.getLast(trajectory).stateTime().state()));
  }
}
