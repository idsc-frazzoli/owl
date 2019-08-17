package ch.ethz.idsc.owl.rrts.adapter;

import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidContinuityCostFunction;
import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidRrtsNdType;
import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransitionSpace;
import ch.ethz.idsc.owl.rrts.RrtsNodeCollections;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
import ch.ethz.idsc.owl.rrts.core.Rrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ContinuityCostFunctionTest extends TestCase {
  public void testSingle() {
    Rrts rrts = new DefaultRrts( //
        ClothoidTransitionSpace.INSTANCE, //
        new RrtsNodeCollections(ClothoidRrtsNdType.INSTANCE, Tensors.vector(0, 0, 0), Tensors.vector(10, 10, 2 * Math.PI)), //
        EmptyTransitionRegionQuery.INSTANCE, ClothoidContinuityCostFunction.INSTANCE);
    rrts.insertAsNode(Tensors.vector(0, 0, 0), 0);
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1, 1, Math.PI / 2), 0).get();
    assertEquals(RealScalar.ZERO, n1.costFromRoot());
  }

  public void testMultiple() {
    Rrts rrts = new DefaultRrts( //
        ClothoidTransitionSpace.INSTANCE, //
        new RrtsNodeCollections(ClothoidRrtsNdType.INSTANCE, Tensors.vector(0, 0, 0), Tensors.vector(10, 10, 2 * Math.PI)), //
        EmptyTransitionRegionQuery.INSTANCE, ClothoidContinuityCostFunction.INSTANCE);
    rrts.insertAsNode(Tensors.vector(0, 0, 0), 0);
    rrts.insertAsNode(Tensors.vector(1, 0, 0), 0);
    rrts.insertAsNode(Tensors.vector(2, 0, 0), 0);
    RrtsNode n3 = rrts.insertAsNode(Tensors.vector(3, 1, Math.PI / 2), 0).get();
    Chop._03.requireClose(RealScalar.ONE, n3.costFromRoot());
  }
}
