// code by jph
package ch.ethz.idsc.sophus.app.clt;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class FindZeroTest extends TestCase {
  public void testSimple() {
    FindZero findZero = new FindZero(Cos.FUNCTION, Sign::isPositive, Chop._12);
    Chop._10.requireClose(findZero.between(RealScalar.of(0.0), RealScalar.of(4.0)), Pi.HALF);
    Chop._10.requireClose(findZero.between(RealScalar.of(1.0), RealScalar.of(4.0)), Pi.HALF);
    Chop._10.requireClose(findZero.between(RealScalar.of(1.0), RealScalar.of(2.0)), Pi.HALF);
  }

  public void testLinear() {
    Scalar scalar = FindZero.linear(RealScalar.of(10), RealScalar.of(11), RealScalar.of(5), RealScalar.of(-2));
    assertEquals(scalar, RationalScalar.of(75, 7));
  }

  public void testOther() {
    Scalar scalar = FindZero.linear(RealScalar.of(5), RealScalar.of(6), RealScalar.of(2), RealScalar.of(-1));
    assertEquals(scalar, RationalScalar.of(5 * 3 + 2, 3));
  }
}
