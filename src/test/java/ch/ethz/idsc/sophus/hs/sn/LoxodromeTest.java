// code by jph
package ch.ethz.idsc.sophus.hs.sn;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class LoxodromeTest extends TestCase {
  public void testSimple() {
    ScalarTensorFunction scalarTensorFunction = Loxodrome.of(RealScalar.of(.1));
    Tensor tensor = Subdivide.of(-1, 100, 60).map(scalarTensorFunction);
    assertFalse(tensor.stream() //
        .map(Norm._2::ofVector) //
        .anyMatch(scalar -> !Chop._12.close(RealScalar.ONE, scalar)));
  }
}
