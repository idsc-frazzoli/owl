// code by jph
package ch.ethz.idsc.sophus.lie.so3;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class So3UniformResampleTest extends TestCase {
  public void testSimple() {
    So3UniformResample.of(RealScalar.ONE);
  }
}
