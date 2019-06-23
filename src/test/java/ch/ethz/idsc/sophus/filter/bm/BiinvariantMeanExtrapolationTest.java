// code by jph
package ch.ethz.idsc.sophus.filter.bm;

import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.win.DirichletWindow;
import junit.framework.TestCase;

public class BiinvariantMeanExtrapolationTest extends TestCase {
  public void testFIR2() {
    TensorUnaryOperator causalFilter = BiinvariantMeanExtrapolation.of(RnBiinvariantMean.INSTANCE, DirichletWindow.FUNCTION);
    {
      Tensor tensor = causalFilter.apply(Tensors.vector(1, 2));
      // FIXME OB should be 3 but is 7/3
      System.out.println(tensor);
    }
  }
}
