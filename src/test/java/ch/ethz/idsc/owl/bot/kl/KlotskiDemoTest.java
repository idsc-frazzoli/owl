// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.util.List;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class KlotskiDemoTest extends TestCase {
  public void testSimple() {
    KlotskiProblem klotskiProblem = new KlotskiProblem() {
      @Override
      public Tensor getBoard() {
        return Huarong.ONLY_18_STEPS.getBoard();
      }

      @Override
      public Tensor size() {
        return Tensors.vector(7, 6);
      }

      @Override
      public Tensor getGoal() {
        return Tensors.vector(0, 3, 2);
      }

      @Override
      public String name() {
        return "noname";
      }
    };
    List<StateTime> list = new KlotskiDemo(klotskiProblem, KlotskiPlot.HUARONG).compute();
    assertEquals(list.size(), 11);
  }
}
