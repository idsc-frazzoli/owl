package ch.ethz.idsc.owl.rrts.adapter;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.bot.rn.rrts.EuclideanNdType;
import ch.ethz.idsc.owl.rrts.NdTypeRrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
import ch.ethz.idsc.owl.rrts.core.Rrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class LengthCostFunctionTest extends TestCase {
  public void testSingle() {
    Rrts rrts = new DefaultRrts( //
        RnTransitionSpace.INSTANCE, //
        NdTypeRrtsNodeCollection.of(EuclideanNdType.INSTANCE, Tensors.vector(0, 0), Tensors.vector(10, 10)), //
        EmptyTransitionRegionQuery.INSTANCE, LengthCostFunction.INSTANCE);
    rrts.insertAsNode(Tensors.vector(0, 0), 0);
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1, 0), 0).get();
    assertEquals(RealScalar.ONE, n1.costFromRoot());
  }

  public void testMultiple() {
    Rrts rrts = new DefaultRrts( //
        RnTransitionSpace.INSTANCE, //
        NdTypeRrtsNodeCollection.of(EuclideanNdType.INSTANCE, Tensors.vector(0, 0), Tensors.vector(10, 10)), //
        EmptyTransitionRegionQuery.INSTANCE, LengthCostFunction.INSTANCE);
    rrts.insertAsNode(Tensors.vector(0, 0), 0);
    rrts.insertAsNode(Tensors.vector(1, 0), 0);
    rrts.insertAsNode(Tensors.vector(2, 0), 0);
    RrtsNode n3 = rrts.insertAsNode(Tensors.vector(2, 1), 0).get();
    assertEquals(RealScalar.of(3), n3.costFromRoot());
  }
}
