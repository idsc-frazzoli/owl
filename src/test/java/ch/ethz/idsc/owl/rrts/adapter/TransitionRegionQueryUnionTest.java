// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.IOException;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.math.region.BallRegion;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class TransitionRegionQueryUnionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    TransitionRegionQuery trq1 = //
        new SampledTransitionRegionQuery(new BallRegion(Tensors.vector(0, 0), RealScalar.ONE), RealScalar.of(0.1));
    TransitionRegionQuery trq2 = //
        new SampledTransitionRegionQuery(new BallRegion(Tensors.vector(2, 0), RealScalar.ONE), RealScalar.of(0.1));
    TransitionRegionQuery transitionRegionQuery = Serialization.copy(TransitionRegionQueryUnion.wrap(trq1, trq2));
    {
      Transition transition = RnTransitionSpace.INSTANCE.connect( //
          Tensors.vector(-2, 0), //
          Tensors.vector(-2, 1));
      assertTrue(transitionRegionQuery.isDisjoint(transition));
      assertTrue(trq1.isDisjoint(transition));
      assertTrue(trq2.isDisjoint(transition));
    }
    {
      Transition transition = RnTransitionSpace.INSTANCE.connect( //
          Tensors.vector(0, -2), //
          Tensors.vector(0, 2));
      assertFalse(transitionRegionQuery.isDisjoint(transition));
      assertFalse(trq1.isDisjoint(transition));
      assertTrue(trq2.isDisjoint(transition));
    }
    {
      Transition transition = RnTransitionSpace.INSTANCE.connect( //
          Tensors.vector(2, -2), //
          Tensors.vector(2, 2));
      assertFalse(transitionRegionQuery.isDisjoint(transition));
      assertTrue(trq1.isDisjoint(transition));
      assertFalse(trq2.isDisjoint(transition));
    }
  }
}
