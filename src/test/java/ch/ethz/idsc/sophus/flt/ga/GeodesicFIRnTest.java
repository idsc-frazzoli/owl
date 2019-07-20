// code by jph
package ch.ethz.idsc.sophus.flt.ga;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Mean;
import junit.framework.TestCase;

public class GeodesicFIRnTest extends TestCase {
  public void testSimple() {
    Tensor s0 = Tensors.vector(1, 2, 3);
    Tensor s1 = Tensors.vector(2, 2, 0);
    Tensor s2 = Tensors.vector(3, 3, 3);
    Tensor s3 = Tensors.vector(9, 3, 2);
    TensorUnaryOperator tensorUnaryOperator = GeodesicFIRn.of(Mean::of, RnGeodesic.INSTANCE, 2, RationalScalar.of(1, 2));
    tensorUnaryOperator.apply(s0);
    tensorUnaryOperator.apply(s1);
    tensorUnaryOperator.apply(s2);
    Tensor tensor = tensorUnaryOperator.apply(s3);
    Tensor p = Mean.of(Tensors.of(s1, s2));
    Tensor q = s3;
    assertEquals(tensor, RnGeodesic.INSTANCE.split(p, q, RationalScalar.HALF));
    ExactTensorQ.require(tensor);
  }
}
