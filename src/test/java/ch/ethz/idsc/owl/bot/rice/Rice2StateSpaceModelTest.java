// code by jph
package ch.ethz.idsc.owl.bot.rice;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class Rice2StateSpaceModelTest extends TestCase {
  public void testFails() {
    Rice2StateSpaceModel.of(RealScalar.ZERO);
    Rice2StateSpaceModel.of(RealScalar.of(-1));
  }
}
