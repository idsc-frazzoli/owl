// code by jph
package ch.ethz.idsc.tensor;

import java.math.BigInteger;
import java.util.Arrays;

import junit.framework.TestCase;

public class IntegerDigitsTest extends TestCase {
  public void testSimple() {
    assertEquals(IntegerDigits.of(BigInteger.valueOf(+321)), Arrays.asList(3, 2, 1));
    assertEquals(IntegerDigits.of(BigInteger.valueOf(-321)), Arrays.asList(3, 2, 1));
    assertEquals(IntegerDigits.of(BigInteger.valueOf(+123456789)), Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
    assertEquals(IntegerDigits.of(BigInteger.valueOf(-123456789)), Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
  }

  public void testExact() {
    assertEquals(IntegerDigits.of(Scalars.fromString("123456789012345678901234567890")), Tensors.vector(Arrays.asList( //
        1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0)));
  }

  public void testZero() {
    assertEquals(IntegerDigits.of(RealScalar.ZERO), Tensors.vector(Arrays.asList()));
  }

  public void testPrecisionFail() {
    try {
      IntegerDigits.of(RealScalar.of(1.0));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testRationalFail() {
    try {
      IntegerDigits.of(RationalScalar.of(10, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
