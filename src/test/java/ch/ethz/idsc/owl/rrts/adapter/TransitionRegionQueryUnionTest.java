// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.IOException;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class TransitionRegionQueryUnionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    TransitionRegionQuery trq1 = //
        new SampledTransitionRegionQuery(new SphericalRegion(Tensors.vector(0, 0), RealScalar.ONE), RealScalar.of(.1));
    TransitionRegionQuery trq2 = //
        new SampledTransitionRegionQuery(new SphericalRegion(Tensors.vector(2, 0), RealScalar.ONE), RealScalar.of(.1));
    TransitionRegionQuery transitionRegionQuery = Serialization.copy(TransitionRegionQueryUnion.wrap(trq1, trq2));
    assertTrue(transitionRegionQuery.isDisjoint(RnTransitionSpace.INSTANCE.connect(Tensors.vector(-2, 0), Tensors.vector(-2, 1))));
    assertFalse(transitionRegionQuery.isDisjoint(RnTransitionSpace.INSTANCE.connect(Tensors.vector(0, -2), Tensors.vector(0, 2))));
    assertFalse(transitionRegionQuery.isDisjoint(RnTransitionSpace.INSTANCE.connect(Tensors.vector(2, -2), Tensors.vector(2, 2))));
  }
}
