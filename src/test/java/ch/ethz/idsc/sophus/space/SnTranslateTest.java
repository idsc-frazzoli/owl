// code by jph
package ch.ethz.idsc.sophus.space;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SnTranslateTest extends TestCase {
  public void testSimple() {
    Tensor tensor = SnTranslate.translate(UnitVector.of(3, 0), UnitVector.of(3, 1).multiply(RealScalar.of(Math.PI / 2)));
    Chop._12.requireClose(tensor, UnitVector.of(3, 1));
  }
}
