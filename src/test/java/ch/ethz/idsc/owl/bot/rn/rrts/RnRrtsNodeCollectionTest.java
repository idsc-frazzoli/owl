// code by jph
package ch.ethz.idsc.owl.bot.rn.rrts;

import java.util.Collection;
import java.util.Random;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.rrts.adapter.EmptyTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
import ch.ethz.idsc.owl.rrts.core.Rrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.math.sample.BoxRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RnRrtsNodeCollectionTest extends TestCase {
  private static final TransitionSpace TRANSITION_SPACE = RnTransitionSpace.INSTANCE;

  public void testSimple() {
    RrtsNodeCollection rrtsNodeCollection = new RnRrtsNodeCollection(Tensors.vector(0, 0), Tensors.vector(10, 10));
    TransitionRegionQuery transitionRegionQuery = EmptyTransitionRegionQuery.INSTANCE;
    Rrts rrts = new DefaultRrts(TRANSITION_SPACE, rrtsNodeCollection, transitionRegionQuery, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 0).get();
    assertEquals(root.children().size(), 0);
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1, 0), 0).get();
    assertEquals(root.children().size(), 1);
    assertEquals(n1.costFromRoot(), RealScalar.of(1));
    RrtsNode n2 = rrts.insertAsNode(Tensors.vector(1, 1), 0).get();
    assertEquals(root.children().size(), 1);
    assertEquals(n1.children().size(), 1);
    assertEquals(n1.children().iterator().next(), n2);
    assertEquals(n1.costFromRoot(), RealScalar.of(1));
    assertEquals(n2.children().size(), 0);
    assertEquals(n2.costFromRoot(), RealScalar.of(2));
  }

  public void testQuantity() {
    Tensor lbounds = Tensors.fromString("{-5[m], -7[m]}");
    Tensor ubounds = Tensors.fromString("{10[m], 10[m]}");
    RrtsNodeCollection rrtsNodeCollection = new RnRrtsNodeCollection(lbounds, ubounds);
    Random random = new Random();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(lbounds, ubounds);
    for (int count = 0; count < 30; ++count) {
      Tensor tensor = randomSampleInterface.randomSample(random);
      rrtsNodeCollection.insert(RrtsNode.createRoot(tensor, RealScalar.ONE));
    }
    Collection<RrtsNode> collection = rrtsNodeCollection.nearTo(Tensors.fromString("{2[m], 3[m]}"), 10);
    assertEquals(collection.size(), 10);
  }
}
