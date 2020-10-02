// code by jph
package ch.ethz.idsc.sophus.app.sym;

import java.io.IOException;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class SymScalarTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Serialization.copy(SymScalar.leaf(3)).hashCode();
    SymScalar.of(SymScalar.leaf(2), SymScalar.leaf(3), RationalScalar.HALF).hashCode();
  }

  public void testFail() {
    AssertFail.of(() -> SymScalar.of(SymScalar.leaf(2), RealScalar.of(3), RationalScalar.HALF));
  }
}
