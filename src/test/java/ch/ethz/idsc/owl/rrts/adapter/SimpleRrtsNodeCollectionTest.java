// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class SimpleRrtsNodeCollectionTest extends TestCase {
  public void testEuclideanNear() {
    Distribution distribution = UniformDistribution.unit();
    RrtsNodeCollection rrtsNodeCollection = new SimpleRrtsNodeCollection(RnTransitionSpace.INSTANCE, LengthCostFunction.INSTANCE);
    for (int index = 0; index < 200; ++index)
      rrtsNodeCollection.insert(RrtsNode.createRoot(RandomVariate.of(distribution, 3), RealScalar.of(10)));
    Tensor center = Tensors.vector(0.5, 0.5, 0.5);
    for (RrtsNode rrtsNode : rrtsNodeCollection.nearTo(center, 3)) {
      Scalar scalar = Vector2Norm.between(center, rrtsNode.state());
      assertTrue(Scalars.lessThan(scalar, RealScalar.of(0.3)));
    }
    for (RrtsNode rrtsNode : rrtsNodeCollection.nearFrom(center, 3)) {
      Scalar scalar = Vector2Norm.between(center, rrtsNode.state());
      assertTrue(Scalars.lessThan(scalar, RealScalar.of(0.3)));
    }
  }
}
