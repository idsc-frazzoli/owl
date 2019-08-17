// code by jph
package ch.ethz.idsc.sophus.sym;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.Pi;
import junit.framework.TestCase;

public class AnyScalarTest extends TestCase {
  public void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(AnyScalar.INSTANCE);
  }

  public void testMultiply() {
    assertEquals(AnyScalar.INSTANCE.multiply(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(RealScalar.ZERO.multiply(AnyScalar.INSTANCE), RealScalar.ZERO);
    assertEquals(Pi.HALF.multiply(AnyScalar.INSTANCE).toString(), AnyScalar.INSTANCE.toString());
    assertEquals(AnyScalar.INSTANCE.multiply(AnyScalar.INSTANCE).toString(), AnyScalar.INSTANCE.toString());
  }
}
