// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.SmoothingKernel;
import ch.ethz.idsc.owl.math.group.RnGeodesic;
import ch.ethz.idsc.owl.math.group.Se2Geodesic;
import ch.ethz.idsc.owl.math.group.So3Geodesic;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.owl.math.planar.S2Geodesic;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.lie.Rodrigues;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class GeodesicCenterFilterTest extends TestCase {
  public void testSimple() {
    TensorUnaryOperator geodesicCenter = GeodesicCenter.of(RnGeodesic.INSTANCE, BinomialWeights.INSTANCE);
    TensorUnaryOperator geodesicCenterFilter = GeodesicCenterFilter.of(geodesicCenter, 3);
    Tensor linear = Range.of(0, 10);
    Tensor result = geodesicCenterFilter.apply(linear);
    assertEquals(result, linear);
    assertTrue(ExactScalarQ.all(result));
  }

  public void testKernel3() {
    TensorUnaryOperator geodesicCenter = GeodesicCenter.of(RnGeodesic.INSTANCE, BinomialWeights.INSTANCE);
    TensorUnaryOperator geodesicCenterFilter = GeodesicCenterFilter.of(geodesicCenter, 3);
    Tensor signal = UnitVector.of(9, 4);
    Tensor result = geodesicCenterFilter.apply(signal);
    assertTrue(ExactScalarQ.all(result));
    assertEquals(result, Tensors.fromString("{0, 0, 1/16, 15/64, 5/16, 15/64, 1/16, 0, 0}"));
  }

  public void testKernel1() {
    TensorUnaryOperator geodesicCenter = GeodesicCenter.of(RnGeodesic.INSTANCE, BinomialWeights.INSTANCE);
    TensorUnaryOperator geodesicCenterFilter = GeodesicCenterFilter.of(geodesicCenter, 1);
    Tensor signal = UnitVector.of(5, 2);
    Tensor result = geodesicCenterFilter.apply(signal);
    assertTrue(ExactScalarQ.all(result));
    assertEquals(result, Tensors.fromString("{0, 1/4, 1/2, 1/4, 0}"));
  }

  public void testS2() {
    TensorUnaryOperator geodesicCenter = GeodesicCenter.of(S2Geodesic.INSTANCE, SmoothingKernel.HANN);
    TensorUnaryOperator geodesicCenterFilter = GeodesicCenterFilter.of(geodesicCenter, 1);
    Distribution distribution = NormalDistribution.standard();
    TensorUnaryOperator tensorUnaryOperator = Normalize.with(Norm._2);
    Tensor tensor = Tensor.of(RandomVariate.of(distribution, 10, 3).stream().map(tensorUnaryOperator));
    Tensor result = geodesicCenterFilter.apply(tensor);
    assertEquals(Dimensions.of(tensor), Dimensions.of(result));
  }

  public void testSo3() {
    TensorUnaryOperator geodesicCenter = GeodesicCenter.of(So3Geodesic.INSTANCE, SmoothingKernel.HAMMING);
    TensorUnaryOperator geodesicCenterFilter = GeodesicCenterFilter.of(geodesicCenter, 1);
    Distribution distribution = UniformDistribution.unit();
    Tensor tensor = Tensor.of(RandomVariate.of(distribution, 10, 3).stream().map(Rodrigues::exp));
    Tensor result = geodesicCenterFilter.apply(tensor);
    assertEquals(Dimensions.of(tensor), Dimensions.of(result));
  }

  public void testData() {
    String resource = "/dubilab/app/pose/2r/20180820T165637_1.csv";
    Tensor table = ResourceData.of(resource);
    Tensor xyz = Tensor.of(table.stream().map(row -> row.extract(1, 4)));
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
      TensorUnaryOperator tensorUnaryOperator = //
          GeodesicCenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, smoothingKernel), 3);
      Tensor res = tensorUnaryOperator.apply(xyz);
      Tensor xy = Tensor.of(xyz.stream().map(Extract2D::of));
      Tensor uv = Tensor.of(res.stream().map(Extract2D::of));
      Tensor dif = Flatten.of(xy.subtract(uv));
      assertTrue(Scalars.lessThan(Norm.INFINITY.of(dif), RealScalar.of(.5)));
    }
  }

  public void testFail() {
    try {
      GeodesicCenterFilter.of(null, 1);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
