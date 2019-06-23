// code by jph
package ch.ethz.idsc.sophus.filter.bm;

import ch.ethz.idsc.sophus.filter.CausalFilter;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.win.DirichletWindow;
import junit.framework.TestCase;

public class BiinvariantMeanFIRnTest extends TestCase {
  // FIXME OB
  public void testFIR2() {
    TensorUnaryOperator causalFilter = CausalFilter.of(() -> //
    BiinvariantMeanFIRn.of(RnGeodesic.INSTANCE, RnBiinvariantMean.INSTANCE, DirichletWindow.FUNCTION, 2, RationalScalar.HALF));
    {
      Tensor tensor = causalFilter.apply(UnitVector.of(10, 0));
      System.out.println(tensor);
      // assertEquals(tensor, Tensors.fromString("{1, 0, -1/2, 0, 0, 0, 0, 0, 0, 0}"));
      // ExactTensorQ.require(tensor);
    }
    {
      // Tensor tensor = causalFilter.apply(UnitVector.of(10, 1));
      // assertEquals(tensor, Tensors.fromString("{0, 1, 1, -1/2, 0, 0, 0, 0, 0, 0}"));
      // ExactTensorQ.require(tensor);
    }
  }
}
