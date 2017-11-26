// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class Se2StateSpaceModelTest extends TestCase {
  public void testSimple() {
    StateSpaceModel stateSpaceModel = Se2StateSpaceModel.INSTANCE;
    assertEquals(stateSpaceModel.getLipschitz(), RealScalar.ONE);
  }
}
