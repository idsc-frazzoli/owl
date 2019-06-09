// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class So2SkewTest extends TestCase {
  public void testFunctionM() {
    Tensor m1 = So2Skew.of(RealScalar.of(-.1));
    Tensor m2 = So2Skew.of(RealScalar.of(+.1));
    Chop._10.requireClose(m1, Transpose.of(m2));
  }
}
