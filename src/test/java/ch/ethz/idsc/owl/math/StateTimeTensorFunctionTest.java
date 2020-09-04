// code by jph
package ch.ethz.idsc.owl.math;

import java.util.function.Function;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class StateTimeTensorFunctionTest extends TestCase {
  public void testWithTime() {
    Function<StateTime, Tensor> sttf = StateTime::joined;
    Tensor key = sttf.apply(new StateTime(Tensors.vector(1, 2), RealScalar.of(3)));
    assertEquals(key, Tensors.vector(1, 2, 3));
  }

  public void testState() {
    Tensor key = StateTimeTensorFunction.state(tensor -> tensor.multiply(RealScalar.of(5))) //
        .apply(new StateTime(Tensors.vector(1, 2), RealScalar.of(3)));
    assertEquals(key, Tensors.vector(5, 10));
  }
}
