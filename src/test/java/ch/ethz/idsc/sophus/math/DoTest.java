// code by jph
package ch.ethz.idsc.sophus.math;

import java.util.function.Supplier;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class DoTest extends TestCase {
  public void testSimple() {
    Supplier<Tensor> supplier = new Supplier<Tensor>() {
      int count = 0;

      @Override
      public Tensor get() {
        return Tensors.vector(++count);
      }
    };
    Tensor tensor = Do.of(supplier, 3);
    assertEquals(tensor, Tensors.vector(3));
  }
}
