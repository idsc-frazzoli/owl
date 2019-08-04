// code by jph
package ch.ethz.idsc.sophus.hs.sn;

import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SnMetricTest extends TestCase {
  public void testSimple() {
    Chop._12.requireClose(SnMetric.INSTANCE.distance(UnitVector.of(3, 0), UnitVector.of(3, 1)), Pi.HALF);
    Chop._12.requireClose(SnMetric.INSTANCE.distance(UnitVector.of(3, 0), UnitVector.of(3, 2)), Pi.HALF);
    Chop._12.requireClose(SnMetric.INSTANCE.distance(UnitVector.of(3, 1), UnitVector.of(3, 2)), Pi.HALF);
  }
}
