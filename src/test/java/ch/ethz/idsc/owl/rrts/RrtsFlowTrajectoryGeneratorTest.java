// code by gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import ch.ethz.idsc.owl.bot.rn.RnRrtsNodeCollection;
import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.adapter.EmptyTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
import ch.ethz.idsc.owl.rrts.core.Rrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RrtsFlowTrajectoryGeneratorTest extends TestCase {
  public void testRn() {
    Rrts rrts = new DefaultRrts( //
        RnTransitionSpace.INSTANCE, //
        new RnRrtsNodeCollection(Tensors.vector(0, 0), Tensors.vector(10, 10)), //
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
        generator.createTrajectory(RnTransitionSpace.INSTANCE, sequence, RationalScalar.ZERO, RationalScalar.of(1, 10));
    assertEquals(30, trajectory.size());
    assertTrue(IntStream.range(1, 30).allMatch(i -> {
      TrajectorySample sample = trajectory.get(i);
      return sample.stateTime().time().equals(RationalScalar.of(i, 10)) //
          && sample.stateTime().state().equals(Tensors.of(RationalScalar.of(i, 10), RationalScalar.ZERO)) //
          && sample.getFlow().get().getU().equals(Tensors.vector(1, 0));
    }));
  }

  // TODO add more tests
}
