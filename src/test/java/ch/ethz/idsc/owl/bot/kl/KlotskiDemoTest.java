// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.util.List;

import ch.ethz.idsc.owl.math.state.StateTime;
import junit.framework.TestCase;

public class KlotskiDemoTest extends TestCase {
  public void testSimple() {
    KlotskiProblem klotskiProblem = Huarong.SIMPLE.create();
    KlotskiDemo klotskiDemo = new KlotskiDemo(klotskiProblem);
    List<StateTime> list = klotskiDemo.compute();
    assertEquals(list.size(), 4);
    klotskiDemo.close();
  }
}
