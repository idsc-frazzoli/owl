// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.IOException;

import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidContinuityCostFunction;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class ComboTransitionCostFunctionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    TransitionCostFunction transitionCostFunction = ComboTransitionCostFunction.of( //
        ClothoidContinuityCostFunction.INSTANCE, //
        LengthCostFunction.INSTANCE);
    Serialization.copy(transitionCostFunction);
  }
}
