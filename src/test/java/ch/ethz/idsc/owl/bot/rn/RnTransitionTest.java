// code by jph
package ch.ethz.idsc.owl.bot.rn;

import java.util.List;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RnTransitionTest extends TestCase {
  public void testSimple() {
    RnTransition rnt = new RnTransition(Tensors.vector(2, 0), Tensors.vector(10, 0));
    List<StateTime> list = rnt.sampled(RealScalar.of(100), RealScalar.ZERO, RealScalar.ONE);
    // list.stream().map(StateTime::info).forEach(System.out::println);
    assertEquals(rnt.length(), RealScalar.of(8));
    assertEquals(list.get(7).state(), Tensors.vector(9, 0));
    assertEquals(list.get(7).time(), RealScalar.of(107));
  }
}
