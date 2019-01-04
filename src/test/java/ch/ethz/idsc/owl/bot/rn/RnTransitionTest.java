// code by jph
package ch.ethz.idsc.owl.bot.rn;

import java.io.IOException;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class RnTransitionTest extends TestCase {
  public void testSimple() {
    Transition transition = new RnTransition(Tensors.vector(2, 0), Tensors.vector(10, 0));
    Tensor tensor = transition.sampled(RealScalar.ZERO, RealScalar.ONE);
    // list.stream().map(StateTime::info).forEach(System.out::println);
    assertEquals(transition.length(), RealScalar.of(8));
    assertEquals(tensor.get(7), Tensors.vector(9, 0));
    // assertEquals(tensor.get(7).time(), RealScalar.of(107));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    Transition transition = new RnTransition(Tensors.vector(2, 0), Tensors.vector(10, 0));
    Serialization.copy(transition);
  }
}
