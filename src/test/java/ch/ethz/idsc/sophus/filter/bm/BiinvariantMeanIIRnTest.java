// code by jph
package ch.ethz.idsc.sophus.filter.bm;

import java.io.IOException;

import ch.ethz.idsc.sophus.filter.CausalFilter;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.win.DirichletWindow;
import junit.framework.TestCase;

public class BiinvariantMeanIIRnTest extends TestCase {
  // FIXME OB
  public void testIIR1() throws ClassNotFoundException, IOException {
    TensorUnaryOperator causalFilter = Serialization.copy(CausalFilter.of(() -> //
    new BiinvariantMeanIIRn(RnGeodesic.INSTANCE, RnBiinvariantMean.INSTANCE, DirichletWindow.FUNCTION, 1, RationalScalar.HALF)));
    {
      Tensor tensor = causalFilter.apply(UnitVector.of(10, 0));
      System.out.println(tensor);
      // assertEquals(tensor, Tensors.fromString("{1, 1/2, 1/4, 1/8, 1/16, 1/32, 1/64, 1/128, 1/256, 1/512}"));
      // ExactTensorQ.require(tensor);
    }
    {
      // Tensor tensor = causalFilter.apply(UnitVector.of(10, 1));
      // assertEquals(tensor, Tensors.fromString("{0, 1/2, 1/4, 1/8, 1/16, 1/32, 1/64, 1/128, 1/256, 1/512}"));
      // ExactTensorQ.require(tensor);
    }
  }
}
