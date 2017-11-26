// code by jph
package ch.ethz.idsc.owl.bot.se2;

import java.util.Collection;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Se2MinTimeGoalManagerTest extends TestCase {
  public void testSimple() {
    CarFlows c = new CarStandardFlows(RealScalar.ONE, RealScalar.ONE);
    Collection<Flow> controls = c.getFlows(3);
    new Se2MinTimeGoalManager(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 3), controls);
    try {
      new Se2MinTimeGoalManager(Tensors.vector(1, 2, 3), Tensors.vector(1, 2, 3), controls);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
