// code by ob
package ch.ethz.idsc.sophus.app.ob;

import ch.ethz.idsc.sophus.filter.ga.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.filter.ga.GeodesicFIRnFilter;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicCausalFilteringFIRTest extends TestCase {
  public void testSimple() {
    Tensor control = Tensors.fromString("{{0,0,0},{1,0,0},{2,0,0},{3,0,0},{4,0,0},{5,0,0}}");
    Scalar alpha = RealScalar.of(0.5);
    final int radius = 3;
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    TensorUnaryOperator tensorUnaryOperator = GeodesicExtrapolation.of(geodesicInterface, SmoothingKernel.GAUSSIAN);
    Tensor actual = GeodesicFIRnFilter.of(tensorUnaryOperator, geodesicInterface, radius, alpha).apply(control);
    assertEquals(control, actual);
  }

  public void testOnlyMeasurement() {
    Tensor control = Tensors.fromString("{{0,0.2,0},{1,0,0},{2,7,0},{3,9,0},{3,0,0},{-1,0,-1}}");
    Scalar alpha = RealScalar.of(1);
    final int radius = 3;
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    TensorUnaryOperator tensorUnaryOperator = GeodesicExtrapolation.of(geodesicInterface, SmoothingKernel.GAUSSIAN);
    Tensor actual = GeodesicFIRnFilter.of(tensorUnaryOperator, geodesicInterface, radius, alpha).apply(control);
    Chop._09.requireClose(control, actual);
  }

  public void testOnlyPrediction() {
    Tensor control = Tensors.fromString("{{0,0.2,0},{1,0,0},{2,7,0},{3,9,0},{3,0,0},{-1,0,-1}}");
    Scalar alpha = RealScalar.of(0);
    final int radius = 2;
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    TensorUnaryOperator tensorUnaryOperator = GeodesicExtrapolation.of(geodesicInterface, SmoothingKernel.GAUSSIAN);
    Tensor actual = GeodesicFIRnFilter.of(tensorUnaryOperator, geodesicInterface, radius, alpha).apply(control);
    Tensor expected = Tensors.fromString("{{0, 0.2, 0}, {1, 0, 0}, {2.0, -0.2, 0.0}, {3.0, 14.0, 0.0}, {4.0, 11.0, 0.0}, {3.0, -9.0, 0.0}}");
    System.err.println(actual);
    Chop._09.requireClose(expected, actual);
  }
}
