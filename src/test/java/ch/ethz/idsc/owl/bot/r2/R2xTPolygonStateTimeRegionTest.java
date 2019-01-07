// code by jph
package ch.ethz.idsc.owl.bot.r2;

import ch.ethz.idsc.owl.math.map.BijectionFamily;
import ch.ethz.idsc.owl.math.map.So2Family;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class R2xTPolygonStateTimeRegionTest extends TestCase {
  public void testSimple() {
    Tensor polygon = CogPoints.of(4, RealScalar.of(1.0), RealScalar.of(0.3));
    // ---
    BijectionFamily bijectionFamily = new So2Family(s -> s);
    Region<StateTime> cog0 = new R2xTPolygonStateTimeRegion(polygon, bijectionFamily, null);
    assertTrue(cog0.isMember(new StateTime(Tensors.vector(0, 0), RealScalar.of(0))));
    assertTrue(cog0.isMember(new StateTime(Tensors.vector(0, 0), RealScalar.of(1))));
    assertTrue(cog0.isMember(new StateTime(Tensors.vector(.8, .1), RealScalar.of(0))));
    assertFalse(cog0.isMember(new StateTime(Tensors.vector(.8, .1), RealScalar.of(.2))));
  }
}
