// code by ob
package ch.ethz.idsc.sophus.filter;

import junit.framework.TestCase;

public class NonuniformFixedIntervalGeodesicCenterFilterTest extends TestCase {
  public void testSimple() {
    // TODO OB: Write Tests as soon as the filter pass their tests
    // intended Tests
    // 1) trivial: Small interval => return control
    // 2) simple: interval including next and prev => return control[1, cl-1}
    // 3) Arbitrary sequence
    // 4) linear function: e.g. x = y
  }
}
