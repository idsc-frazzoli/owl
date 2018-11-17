// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

// Mathematica gives the result for 1/4, 1/3, 1/2 in exact precision
public class BlackmanNuttallWindowTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator windowFunction = BlackmanNuttallWindow.function();
    Scalar scalar = windowFunction.apply(RationalScalar.HALF);
    assertTrue(Chop._10.close(scalar, Scalars.fromString("907/2500000")));
  }

  public void testQuarter() {
    ScalarUnaryOperator windowFunction = BlackmanNuttallWindow.function();
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 4));
    assertTrue(Chop._10.close(scalar, Scalars.fromString("17733/78125")));
  }

  public void testThird() {
    ScalarUnaryOperator windowFunction = BlackmanNuttallWindow.function();
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 3));
    assertTrue(Chop._10.close(scalar, Scalars.fromString("122669/2000000")));
  }
}
