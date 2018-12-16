// code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.LieGroupGeodesic;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Group;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import junit.framework.TestCase;

public class GeodesicCausalFilteringTest extends TestCase {
  public void testSimple() {
    GeodesicInterface geodesicInterface = //
        new LieGroupGeodesic(Se2Group.INSTANCE::element, Se2CoveringExponential.INSTANCE);
    // GeodesicCausalFiltering geodesicCausalFiltering = GeodesicCausalFiltering.se2(Tensors.of( //
    // Tensors.vector(1, 2, 0.5), //
    // Tensors.vector(4, 5, 0.5), //
    // Tensors.vector(6.164525387368366, 8.648949142895502, 0.75)), //
    // 1, 3);
    // assertTrue((RealScalar.ZERO.equals(geodesicCausalFiltering.log.get(geodesicCausalFiltering.log.length() - 1).Get(1))));
    // // The following test is maybe not useful because it might fail when we change the alpharange or filter
    // assertTrue((RealScalar.of(0.98075).equals(geodesicCausalFiltering.log.get(0).Get(1))));
  }
}
