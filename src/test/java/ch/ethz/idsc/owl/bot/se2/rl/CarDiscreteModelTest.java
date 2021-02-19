// code by jph
package ch.ethz.idsc.owl.bot.se2.rl;

import ch.ethz.idsc.tensor.Tensor;
import junit.framework.TestCase;

public class CarDiscreteModelTest extends TestCase {
  public void testSimple5() {
    CarDiscreteModel cdm = new CarDiscreteModel(5);
    Tensor states = cdm.states();
    assertEquals(states.length(), 60 + 1);
  }

  public void testSimple6() {
    CarDiscreteModel cdm = new CarDiscreteModel(6);
    Tensor states = cdm.states();
    assertEquals(states.length(), 360 + 1);
  }
}
