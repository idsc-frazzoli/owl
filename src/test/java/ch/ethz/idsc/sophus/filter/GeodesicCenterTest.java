// code by jph
package ch.ethz.idsc.sophus.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGeodesic;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.sophus.math.win.WindowCenterSampler;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class GeodesicCenterTest extends TestCase {
  private static final IntegerTensorFunction CONSTANT = //
      i -> Array.of(k -> RationalScalar.of(1, 2 * i + 1), 2 * i + 1);

  public void testSimple() {
    // function generates window to compute mean: all points in window have same weight
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(RnGeodesic.INSTANCE, CONSTANT);
    for (int index = 0; index < 9; ++index) {
      Tensor apply = tensorUnaryOperator.apply(UnitVector.of(9, index));
      assertEquals(apply, RationalScalar.of(1, 9));
    }
  }

  public void testSe2() throws ClassNotFoundException, IOException {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
      TensorUnaryOperator tensorUnaryOperator = //
          Serialization.copy(GeodesicCenter.of(Se2Geodesic.INSTANCE, smoothingKernel));
      Distribution distribution = UniformDistribution.unit();
      Tensor sequence = RandomVariate.of(distribution, 7, 3);
      Tensor tensor = tensorUnaryOperator.apply(sequence);
      assertEquals(Dimensions.of(tensor), Arrays.asList(3));
    }
  }

  public void testEvenFail() {
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(RnGeodesic.INSTANCE, CONSTANT);
    for (int index = 0; index < 9; ++index)
      try {
        tensorUnaryOperator.apply(Array.zeros(2 * index));
        fail();
      } catch (Exception exception) {
        // ---
      }
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(Se2CoveringGeodesic.INSTANCE, CONSTANT);
    Serialization.copy(tensorUnaryOperator);
  }

  public void testFail() {
    try {
      GeodesicCenter.of(RnGeodesic.INSTANCE, (WindowCenterSampler) null);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      GeodesicCenter.of(RnGeodesic.INSTANCE, (ScalarUnaryOperator) null);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      GeodesicCenter.of(null, CONSTANT);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testSplitsMean() {
    Function<Integer, Tensor> centerWindowSampler = WindowCenterSampler.of(SmoothingKernel.DIRICHLET);
    {
      Tensor tensor = GeodesicCenter.splits(centerWindowSampler.apply(1));
      assertEquals(tensor, Tensors.fromString("{1/3}"));
    }
    {
      Tensor tensor = GeodesicCenter.splits(centerWindowSampler.apply(2));
      assertEquals(tensor, Tensors.fromString("{1/2, 1/5}"));
    }
    {
      Tensor tensor = GeodesicCenter.splits(centerWindowSampler.apply(3));
      assertEquals(tensor, Tensors.fromString("{1/2, 1/3, 1/7}"));
    }
  }

  public void testSplitsBinomial() {
    {
      Tensor tensor = GeodesicCenter.splits(BinomialWeights.INSTANCE.apply(1));
      assertEquals(tensor, Tensors.fromString("{1/2}"));
    }
    {
      Tensor tensor = GeodesicCenter.splits(BinomialWeights.INSTANCE.apply(2));
      assertEquals(tensor, Tensors.fromString("{4/5, 3/8}"));
    }
    {
      Tensor tensor = GeodesicCenter.splits(BinomialWeights.INSTANCE.apply(3));
      assertEquals(tensor, Tensors.fromString("{6/7, 15/22, 5/16}"));
    }
  }

  public void testFailEven() {
    try {
      GeodesicCenter.splits(Tensors.vector(1, 2));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNonSymmetric() {
    try {
      GeodesicCenter.splits(Tensors.vector(1, 2, 2));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
