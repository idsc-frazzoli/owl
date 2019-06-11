// code by jph
package ch.ethz.idsc.sophus.lie.so2;

import java.io.IOException;

import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class So2Test extends TestCase {
  public void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(So2.MOD);
  }
}
