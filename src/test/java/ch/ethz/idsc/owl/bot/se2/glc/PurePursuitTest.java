// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class PurePursuitTest extends TestCase {
  public void testMatch2() {
    Tensor curve = Tensors.fromString("{{-0.4},{0.6},{1.4},{2.2}}");
    Optional<Tensor> optional = PurePursuit.beacon(curve, RealScalar.ONE);
    assertTrue(optional.isPresent());
    Tensor point = optional.get();
    assertEquals(point, Tensors.vector(1));
  }

  public void testDistanceFail() {
    Tensor curve = Tensors.fromString("{{-0.4},{0.6},{1.4},{2.2}}");
    Optional<Tensor> optional = PurePursuit.beacon(curve, RealScalar.of(3.3));
    assertFalse(optional.isPresent());
  }
}
