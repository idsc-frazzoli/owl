// code by jph
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class RnTransitionSpaceTest extends TestCase {
  public void testLength() {
    Transition transition = RnTransitionSpace.INSTANCE.connect( //
        Tensors.fromString("{1[m],2[m]}"), //
        Tensors.fromString("{1[m],6[m]}"));
    assertEquals(transition.length(), Quantity.of(4, "m"));
    ExactScalarQ.require(transition.length());
  }
}
