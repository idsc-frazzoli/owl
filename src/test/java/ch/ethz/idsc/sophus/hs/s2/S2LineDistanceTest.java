// code by jph
package ch.ethz.idsc.sophus.hs.s2;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class S2LineDistanceTest extends TestCase {
  public void testSimple() {
    S2LineDistance s2LineDistance = new S2LineDistance(Tensors.vector(1, 0, 0), Tensors.vector(0, 1, 0));
    Chop._12.requireAllZero(s2LineDistance.norm(Tensors.vector(-1, 0, 0)));
    Chop._12.requireClose(s2LineDistance.norm(Tensors.vector(0, 0, +1)), Pi.HALF);
    Chop._12.requireClose(s2LineDistance.norm(Tensors.vector(0, 0, -1)), Pi.HALF);
  }
}
