// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.mat.PositiveDefiniteMatrixQ;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Spd2GeodesicDisplayTest extends TestCase {
  private static final GeodesicDisplay GEODESIC_DISPLAY = Spd2GeodesicDisplay.INSTANCE;

  public void testSimple() {
    Tensor tensor = GEODESIC_DISPLAY.project(Tensors.vector(1, 0.2, -1));
    assertTrue(PositiveDefiniteMatrixQ.ofHermitian(tensor));
    Tensor vector = GEODESIC_DISPLAY.toPoint(tensor);
    Chop._10.requireClose(vector, Tensors.vector(1, 0.2));
    Tensor lift = GEODESIC_DISPLAY.matrixLift(tensor);
    TensorUnaryOperator tensorUnaryOperator = PadRight.zeros(2, 2);
    Chop._10.requireClose(tensorUnaryOperator.apply(lift), tensor);
  }
}
