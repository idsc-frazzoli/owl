// code by jph
package ch.ethz.idsc.owl.data;

import ch.ethz.idsc.tensor.Scalars;
import junit.framework.TestCase;

public class TimeKeeperTest extends TestCase {
  public void testSimple() {
    TimeKeeper tk = new TimeKeeper();
    assertTrue(Scalars.nonZero(tk.now()));
  }
}
