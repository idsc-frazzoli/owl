// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.io.IOException;

import ch.ethz.idsc.owl.math.region.ImplicitFunctionRegion;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class R2BubblesTest extends TestCase {
  public void testSerializable() throws ClassNotFoundException, IOException {
    ImplicitFunctionRegion copy = Serialization.copy(R2Bubbles.INSTANCE);
    assertFalse(copy.isMember(Tensors.vector(1, 2)));
  }
}
