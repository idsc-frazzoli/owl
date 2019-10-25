// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import junit.framework.TestCase;

public class Hermite3SubdivisionsTest extends TestCase {
  public void testSimple() {
    TestHelper.check(RnHermite3Subdivisions.a1(), Hermite3Subdivisions.a1(RnGroup.INSTANCE, RnExponential.INSTANCE, RnBiinvariantMean.INSTANCE));
    TestHelper.check(RnHermite3Subdivisions.a2(), Hermite3Subdivisions.a2(RnGroup.INSTANCE, RnExponential.INSTANCE, RnBiinvariantMean.INSTANCE));
  }
}
