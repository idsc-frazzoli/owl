// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class Hermite1SubdivisionsTest extends TestCase {
  public void testSimple() {
    TestHelper.check( //
        RnHermite1Subdivisions.instance(), //
        Hermite1Subdivisions.of(RnGroup.INSTANCE, RnExponential.INSTANCE));
  }

  public void testParams() {
    Scalar lambda = RationalScalar.of(-1, 16);
    Scalar mu = RationalScalar.of(-1, 3);
    TestHelper.check( //
        RnHermite1Subdivisions.of(lambda, mu), //
        Hermite1Subdivisions.of(RnGroup.INSTANCE, RnExponential.INSTANCE, lambda, mu));
  }
}
