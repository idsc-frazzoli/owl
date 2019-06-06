// code by jph
package ch.ethz.idsc.sophus.math;

import java.util.function.Function;

import junit.framework.TestCase;

public class MemoFunctionTest extends TestCase {
  public void testSimple() {
    Function<Object, Double> function = MemoFunction.wrap(k -> Math.random());
    double double1 = function.apply("eth");
    double double2 = function.apply("eth");
    assertEquals(double1, double2);
  }
}
