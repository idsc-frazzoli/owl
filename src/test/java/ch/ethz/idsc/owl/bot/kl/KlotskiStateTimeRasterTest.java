// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import junit.framework.TestCase;

public class KlotskiStateTimeRasterTest extends TestCase {
  public void testSimple() {
    for (Huarong huarong : Huarong.values()) {
      KlotskiProblem klotskiProblem = huarong.create();
      Tensor board = klotskiProblem.startState();
      Tensor key = KlotskiStateTimeRaster.INSTANCE.convertToKey(new StateTime(board, RealScalar.ONE));
      assertEquals(board, key);
    }
  }
}
