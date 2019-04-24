package ch.ethz.idsc.sophus.app.ob;

import ch.ethz.idsc.sophus.filter.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.filter.GeodesicIIRnFilter;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.Assert;
import junit.framework.TestCase;

public class GeodesicCausalFilteringFIRTest extends TestCase {
  public void testSimple() {
    Tensor control = Tensors.fromString("{{0,0,0},{1,0,0},{2,0,0},{3,0,0},{4,0,0},{5,0,0}}");
    Scalar alpha = RealScalar.of(0.5);
    final int radius = 3;
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    TensorUnaryOperator tensorUnaryOperator = GeodesicExtrapolation.of(geodesicInterface, SmoothingKernel.GAUSSIAN);
    Tensor actual = GeodesicIIRnFilter.of(tensorUnaryOperator, geodesicInterface, radius, alpha).apply(control);
    Assert.assertEquals(control, actual);
  }

  public void testOnlyMeasurement() {
    Tensor control = Tensors.fromString("{{0,0.2,0},{1,0,0},{2,7,0},{3,9,0},{3,0,0},{-1,0,-1}}");
    Scalar alpha = RealScalar.of(1);
    final int radius = 3;
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    TensorUnaryOperator tensorUnaryOperator = GeodesicExtrapolation.of(geodesicInterface, SmoothingKernel.GAUSSIAN);
    Tensor actual = GeodesicIIRnFilter.of(tensorUnaryOperator, geodesicInterface, radius, alpha).apply(control);
    System.err.println(actual);
    Chop._09.requireClose(control, actual);
  }

  public void testOnlyPrediction() {
    Tensor control = Tensors.fromString("{{0,0.2,0},{1,0,0},{2,7,0},{3,9,0},{3,0,0},{-1,0,-1}}");
    Scalar alpha = RealScalar.of(0);
    final int radius = 3;
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    TensorUnaryOperator tensorUnaryOperator = GeodesicExtrapolation.of(geodesicInterface, SmoothingKernel.GAUSSIAN);
    Tensor actual = GeodesicIIRnFilter.of(tensorUnaryOperator, geodesicInterface, radius, alpha).apply(control);
    Tensor expected = Tensors.fromString(
        "{{0, 0.2, 0}, {1, 0, 0}, {2.0, -0.2, 0.0}, {3.0, -0.3999999999999999, 0.0}, {4.0, -0.5999999999999998, 0.0}, {5.0, -0.7999999999999996, 0.0}}");
    Chop._09.requireClose(expected, actual);
  }
}
