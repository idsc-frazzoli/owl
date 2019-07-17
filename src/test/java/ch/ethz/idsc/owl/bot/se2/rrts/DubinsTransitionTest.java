// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.IOException;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class DubinsTransitionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    TransitionSpace transitionSpace = Serialization.copy(DubinsTransitionSpace.of(RealScalar.of(2)));
    Transition transition = transitionSpace.connect(Tensors.vector(1, 2, 3), Tensors.vector(3, -8, 1));
    TransitionWrap transitionWrap = transition.wrapped(RealScalar.of(.3));
    assertEquals(transitionWrap.samples().length(), transitionWrap.spacing().length());
  }
}
