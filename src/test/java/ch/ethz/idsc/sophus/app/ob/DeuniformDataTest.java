// code by jph
package ch.ethz.idsc.sophus.app.ob;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Sort;
import junit.framework.TestCase;

public class DeuniformDataTest extends TestCase {
  public void testSimple() {
    Tensor tensor = DeuniformData.of(Range.of(0, 100), RealScalar.of(0.15));
    assertEquals(Sort.of(tensor), tensor);
    assertTrue(tensor.length() < 40);
  }
}
