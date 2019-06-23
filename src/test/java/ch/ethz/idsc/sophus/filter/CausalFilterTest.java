// code by jph
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.filter.ga.GeodesicFIR2Filter;
import ch.ethz.idsc.sophus.filter.ga.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import junit.framework.TestCase;

public class CausalFilterTest extends TestCase {
  public void testIIR1() {
    CausalFilter causalFilter = new CausalFilter(() -> new GeodesicIIR1Filter(RnGeodesic.INSTANCE, RationalScalar.HALF));
    {
      Tensor tensor = causalFilter.apply(UnitVector.of(10, 0));
      assertEquals(tensor, Tensors.fromString("{1, 1/2, 1/4, 1/8, 1/16, 1/32, 1/64, 1/128, 1/256, 1/512}"));
      ExactTensorQ.require(tensor);
    }
    {
      Tensor tensor = causalFilter.apply(UnitVector.of(10, 1));
      assertEquals(tensor, Tensors.fromString("{0, 1/2, 1/4, 1/8, 1/16, 1/32, 1/64, 1/128, 1/256, 1/512}"));
      ExactTensorQ.require(tensor);
    }
  }

  public void testFIR2() {
    CausalFilter causalFilter = new CausalFilter(() -> new GeodesicFIR2Filter(RnGeodesic.INSTANCE, RationalScalar.HALF));
    {
      // FIXME OB not correct, since infinite impulse response instead of finite
      Tensor tensor = causalFilter.apply(UnitVector.of(10, 0));
      assertEquals(tensor, Tensors.fromString("{1, 1/2, 0, -1/4, -1/4, -1/8, 0, 1/16, 1/16, 1/32}"));
      ExactTensorQ.require(tensor);
    }
    {
      Tensor tensor = causalFilter.apply(UnitVector.of(10, 1));
      assertEquals(tensor, Tensors.fromString("{0, 1/2, 1/2, 1/4, 0, -1/8, -1/8, -1/16, 0, 1/32}"));
      ExactTensorQ.require(tensor);
    }
  }
}
