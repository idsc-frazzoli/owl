// code by jph
package ch.ethz.idsc.tensor.sca.win;

import java.util.Map;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Tally;
import junit.framework.TestCase;

public class TukeyWindowTest extends TestCase {
  public void testSmall() {
    Tensor tensor = Tensors.of(RationalScalar.of(-1, 6), RealScalar.ZERO, RealScalar.of(.01), RationalScalar.of(1, 6));
    Tensor mapped = tensor.map(TukeyWindow.function());
    Map<Tensor, Long> map = Tally.of(mapped);
    assertEquals(map.get(RealScalar.ONE).longValue(), tensor.length());
  }
}
