// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

// Mathematica gives the result for 1/4, 1/3, 1/2 in exact precision
public class BlackmanHarrisWindowTest extends TestCase {
  public void testSimple() {
    WindowFunction windowFunction = BlackmanHarrisWindow.function();
    Scalar scalar = windowFunction.apply(RationalScalar.HALF);
    assertTrue(Chop._10.close(scalar, Scalars.fromString("3/50000")));
  }

  public void testQuarter() {
    WindowFunction windowFunction = BlackmanHarrisWindow.function();
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 4));
    assertTrue(Chop._10.close(scalar, RationalScalar.of(21747, 100000)));
  }

  public void testThird() {
    WindowFunction windowFunction = BlackmanHarrisWindow.function();
    Scalar scalar = windowFunction.apply(RationalScalar.of(1, 3));
    assertTrue(Chop._10.close(scalar, RationalScalar.of(11129, 200000)));
  }
}
