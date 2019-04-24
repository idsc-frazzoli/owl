// code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.Assert;
import junit.framework.TestCase;

public class NonuniformFilterTest extends TestCase {
  public void testSimple() {
    Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/2r/20180820T165637_1.csv").stream() //
        .map(row -> row.extract(0, 4)));
    NonuniformFilter nonuniformFilter = new NonuniformFilter(Se2Geodesic.INSTANCE, RealScalar.of(4), RealScalar.of(1));
    Tensor actual = nonuniformFilter.apply(control);
    Assert.assertEquals(control, actual);
  }
}
