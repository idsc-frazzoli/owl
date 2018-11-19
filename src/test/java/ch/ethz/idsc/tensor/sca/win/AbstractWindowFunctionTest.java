// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class AbstractWindowFunctionTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator scalarUnaryOperator = BartlettWindow.function();
    assertFalse(ExactScalarQ.of(scalarUnaryOperator.apply(RealScalar.of(1.1))));
    assertTrue(ExactScalarQ.of(scalarUnaryOperator.apply(RealScalar.of(2))));
  }
}
