// code by jph
package ch.ethz.idsc.owl.gui.ani;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class AbstractEntityTest extends TestCase {
  public void testSimple() {
    Set<Scalar> queue = new ConcurrentSkipListSet<>();
    Distribution distribution = NormalDistribution.of(5, 10);
    RandomVariate.of(distribution, 100).stream() //
        .map(Scalar.class::cast).forEach(queue::add);
    Scalar last = DoubleScalar.NEGATIVE_INFINITY;
    for (Scalar scalar : queue) {
      assertTrue(Scalars.lessEquals(last, scalar));
      last = scalar;
    }
  }
}
