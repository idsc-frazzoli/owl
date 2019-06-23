// code by jph
package ch.ethz.idsc.sophus.filter;

import java.io.IOException;

import ch.ethz.idsc.sophus.filter.ga.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.filter.ga.GeodesicFIR2;
import ch.ethz.idsc.sophus.filter.ga.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.filter.ga.GeodesicIIR2Filter;
import ch.ethz.idsc.sophus.filter.ga.GeodesicIIRnFilter;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.win.DirichletWindow;
import junit.framework.TestCase;

public class CausalFilterTest extends TestCase {
  public void testIIR1() throws ClassNotFoundException, IOException {
    TensorUnaryOperator causalFilter = //
        Serialization.copy(CausalFilter.of(() -> new GeodesicIIR1Filter(RnGeodesic.INSTANCE, RationalScalar.HALF)));
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

  public void testIIR2a() throws ClassNotFoundException, IOException {
    TensorUnaryOperator causalFilter = //
        Serialization.copy(CausalFilter.of(() -> new GeodesicIIR2Filter(RnGeodesic.INSTANCE, RationalScalar.HALF)));
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

  public void testIIR2b() {
    TensorUnaryOperator geodesicExtrapolation = GeodesicExtrapolation.of(RnGeodesic.INSTANCE, DirichletWindow.FUNCTION);
    TensorUnaryOperator causalFilter = GeodesicIIRnFilter.of(geodesicExtrapolation, RnGeodesic.INSTANCE, 2, RationalScalar.HALF);
    {
      Tensor tensor = causalFilter.apply(UnitVector.of(10, 0));
      System.out.println(tensor);
      // assertEquals(tensor, Tensors.fromString("{1, 1/2, 1/4, 1/8, 1/16, 1/32, 1/64, 1/128, 1/256, 1/512}"));
      // ExactTensorQ.require(tensor);
    }
  }

  public void testFIR2() {
    TensorUnaryOperator causalFilter = CausalFilter.of(() -> GeodesicFIR2.of(RnGeodesic.INSTANCE, RationalScalar.HALF));
    {
      Tensor tensor = causalFilter.apply(UnitVector.of(10, 0));
      assertEquals(tensor, Tensors.fromString("{1, 0, -1/2, 0, 0, 0, 0, 0, 0, 0}"));
      ExactTensorQ.require(tensor);
    }
    {
      Tensor tensor = causalFilter.apply(UnitVector.of(10, 1));
      assertEquals(tensor, Tensors.fromString("{0, 1, 1, -1/2, 0, 0, 0, 0, 0, 0}"));
      ExactTensorQ.require(tensor);
    }
  }

  public void testFailNull() {
    try {
      CausalFilter.of(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
