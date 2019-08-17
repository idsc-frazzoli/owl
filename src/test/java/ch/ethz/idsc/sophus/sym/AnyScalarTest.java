// code by jph
package ch.ethz.idsc.sophus.sym;

import java.io.IOException;

import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class AnyScalarTest extends TestCase {
  public void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(AnyScalar.INSTANCE);
  }
}
