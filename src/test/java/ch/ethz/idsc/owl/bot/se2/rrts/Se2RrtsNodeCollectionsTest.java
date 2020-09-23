// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.util.Collection;
import java.util.Random;

import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathComparator;
import ch.ethz.idsc.sophus.math.sample.BoxRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Append;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Se2RrtsNodeCollectionsTest extends TestCase {
  private static void _check(TransitionSpace transitionSpace) {
    Tensor lbounds = Tensors.fromString("{-5[m], -7[m]}");
    Tensor ubounds = Tensors.fromString("{10[m], 10[m]}");
    RrtsNodeCollection rrtsNodeCollection = //
        Se2RrtsNodeCollections.of(transitionSpace, lbounds, ubounds);
    Random random = new Random();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of( //
        Append.of(lbounds, Pi.VALUE.negate()), //
        Append.of(ubounds, Pi.VALUE));
    for (int count = 0; count < 30; ++count) {
      Tensor tensor = randomSampleInterface.randomSample(random);
      rrtsNodeCollection.insert(RrtsNode.createRoot(tensor, RealScalar.ONE));
    }
    Collection<RrtsNode> collection = rrtsNodeCollection.nearTo(Tensors.fromString("{2[m], 3[m], 1.2}"), 10);
    assertEquals(collection.size(), 10);
  }

  public void testClothoid() {
    _check(ClothoidTransitionSpace.ANALYTIC);
  }

  public void testDubins() {
    _check(DubinsTransitionSpace.of(Quantity.of(2, "m"), DubinsPathComparator.LENGTH));
  }
}
