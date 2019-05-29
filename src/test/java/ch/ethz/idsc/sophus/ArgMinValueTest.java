// code by jph
package ch.ethz.idsc.sophus;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ArgMinValueTest extends TestCase {
  public void testSimple() {
    ArgMinValue argMinValue = ArgMinValue.of(Tensors.vector(3, 2, 3, 4, 5, 1, 2, 3, 4));
    assertEquals(argMinValue.index(), 5);
    assertFalse(argMinValue.index(RationalScalar.HALF).isPresent());
    assertEquals(argMinValue.index(RealScalar.of(123)).get(), (Integer) 5);
    assertEquals(argMinValue.value().get(), RealScalar.ONE);
    assertFalse(argMinValue.value(RealScalar.of(0.1)).isPresent());
    assertEquals(argMinValue.value(RealScalar.of(123)).get(), RealScalar.ONE);
  }
}
