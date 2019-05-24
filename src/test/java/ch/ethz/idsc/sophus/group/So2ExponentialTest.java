// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.Assert;
import junit.framework.TestCase;

public class So2ExponentialTest extends TestCase {
  public void testSimple() {
    Scalar alpha = RealScalar.of(Math.random());
    Assert.assertEquals(alpha, So2Exponential.INSTANCE.log(So2Exponential.INSTANCE.exp(alpha)));
  }
}
