// code by jph
package ch.ethz.idsc.sophus.filter.ga;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
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
    {
      Tensor tensor = tensorUnaryOperator.apply(s0);
      System.out.println(tensor);
    }
    {
      Tensor tensor = tensorUnaryOperator.apply(s1);
      System.out.println(tensor);
    }
    {
      Tensor tensor = tensorUnaryOperator.apply(s2);
      System.out.println(tensor);
    }
    {
      Tensor tensor = tensorUnaryOperator.apply(s3);
      System.out.println(tensor);
      System.out.println("mean=" + Mean.of(Tensors.of(s2, s3)));
    }
  }
}
