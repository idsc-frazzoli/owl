// code by jph
package ch.ethz.idsc.sophus.util;

import java.io.IOException;
import java.util.function.Function;

import ch.ethz.idsc.sophus.flt.ga.IntegerTensorFunction;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class MemoFunctionTest extends TestCase {
  public void testSimple() {
    Function<Object, Double> function = MemoFunction.wrap(k -> Math.random());
    double double1 = function.apply("eth");
    double double2 = function.apply("eth");
    assertEquals(double1, double2);
  }

  public void testSerialization() throws ClassNotFoundException, IOException {
    IntegerTensorFunction itf = i -> Array.zeros(i).unmodifiable();
    Function<Integer, Tensor> function = Serialization.copy(MemoFunction.wrap(itf));
    assertEquals(function.apply(3), Array.zeros(3));
  }

  public void testInception() {
    Function<Object, Double> memo1 = MemoFunction.wrap(k -> Math.random());
    Function<Object, Double> memo2 = MemoFunction.wrap(memo1);
    assertEquals(memo1, memo2);
  }

  public void testFailNull() {
    try {
      MemoFunction.wrap(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
