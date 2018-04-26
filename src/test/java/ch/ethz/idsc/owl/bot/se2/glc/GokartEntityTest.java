// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.tensor.alg.MatrixQ;
import junit.framework.TestCase;

public class GokartEntityTest extends TestCase {
  public void testFootprint() {
    assertTrue(MatrixQ.of(GokartEntity.SHAPE));
  }
}
