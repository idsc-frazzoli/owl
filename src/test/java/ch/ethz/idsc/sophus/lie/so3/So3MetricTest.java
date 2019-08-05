// code by jph
package ch.ethz.idsc.sophus.lie.so3;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class So3MetricTest extends TestCase {
  public void testSimple() {
    Tensor vector = Tensors.vector(0.5, 0.5, 0.5);
    Scalar distance = So3Metric.INSTANCE.distance( //
        So3Exponential.INSTANCE.exp(Tensors.vector(0, 0, 0)), //
        So3Exponential.INSTANCE.exp(vector));
    Chop._15.requireClose(distance, Norm._2.ofVector(vector));
  }
}
