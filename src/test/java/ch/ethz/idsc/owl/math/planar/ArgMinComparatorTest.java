// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ArgMinComparatorTest extends TestCase {
  public void testSimple() {
    int r1 = ArgMinComparator.INSTANCE.compare(Tensors.vector(1), Tensors.vector(2));
    int r2 = Integer.compare(1, 2);
    int r3 = Scalars.compare(RealScalar.of(1), RealScalar.of(2));
    assertEquals(r1, r2);
    assertEquals(r1, r3);
  }
}
