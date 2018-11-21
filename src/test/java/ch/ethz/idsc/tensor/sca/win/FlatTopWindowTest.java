// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

// Mathematica gives the result for 1/4, 1/3, 1/2 in exact precision
public class FlatTopWindowTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator windowFunction = FlatTopWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.HALF);
    assertTrue(Chop._10.close(scalar, Scalars.fromString("-210527/500000000")));
  }

  public void testQuarter() {
    ScalarUnaryOperator windowFunction = FlatTopWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 4));
    assertTrue(Chop._10.close(scalar, Scalars.fromString("-54736843/1000000000")));
  }

  public void testThird() {
    ScalarUnaryOperator windowFunction = FlatTopWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 3));
    assertTrue(Chop._10.close(scalar, Scalars.fromString("-51263159/1000000000")));
  }

  public void testTenth() {
    ScalarUnaryOperator windowFunction = FlatTopWindow.FUNCTION;
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 10));
    assertTrue(Chop._10.close(scalar, Scalars.fromString("0.60687214957621189799")));
  }
}
