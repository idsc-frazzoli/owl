// code by jph
package ch.ethz.idsc.owl.plot;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class ClipExpandTest extends TestCase {
  public void testSimple() {
    ClipExpand ce = new ClipExpand(Clip.function(1.2, 1.8), 8);
    assertEquals(ce.clip.min(), RealScalar.ONE);
    assertEquals(ce.clip.max(), RationalScalar.of(9, 5));
    // System.out.println(ce.clip.min() + " " + ce.clip.max());
  }
}
