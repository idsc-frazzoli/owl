// code by jph
package ch.ethz.idsc.owl.bot.se2.rl;

import java.util.Random;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.ExponentialDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class ScanToStateTest extends TestCase {
  public void testSimple() {
    Tensor res = ScanToState.of(Tensors.vector(1, 2, 3));
    assertEquals(res.length(), 2);
  }

  public void testCollision() {
    Tensor res = ScanToState.of(Tensors.vector(0, 0, 0));
    assertEquals(res.length(), 2);
    assertTrue(res.get(0).length() < 2);
  }

  public void testUnique() {
    Random random = new Random();
    Distribution distribution = ExponentialDistribution.standard();
    for (int count = 0; count < 100; ++count) {
      Tensor range = RandomVariate.of(distribution, 2 + random.nextInt(4));
      Tensor st1 = ScanToState.of(range);
      Tensor st2 = ScanToState.of(Reverse.of(range));
      assertEquals(Last.of(st1).negate(), Last.of(st2));
    }
  }
}
