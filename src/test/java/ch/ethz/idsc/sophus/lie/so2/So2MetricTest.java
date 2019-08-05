// code by gjoel
package ch.ethz.idsc.sophus.lie.so2;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.Pi;
import junit.framework.TestCase;

public class So2MetricTest extends TestCase {
  public void testTrivial1() {
    Scalar err = So2Metric.INSTANCE.distance(RealScalar.ONE, RealScalar.ONE);
    assertEquals(RealScalar.ZERO, err);
  }

  public void testTrivial2() {
    Scalar err = So2Metric.INSTANCE.distance(RealScalar.ZERO, Pi.TWO);
    assertEquals(RealScalar.ZERO, err);
  }

  public void testPositive() {
    Scalar err = So2Metric.INSTANCE.distance(RealScalar.ONE, RealScalar.of(2));
    assertEquals(RealScalar.ONE, err);
  }

  public void testNegative() {
    Scalar err = So2Metric.INSTANCE.distance(RealScalar.ONE.negate(), RealScalar.of(2).negate());
    assertEquals(RealScalar.ONE, err);
  }

  public void testMixed1() {
    Scalar err = So2Metric.INSTANCE.distance(RealScalar.ONE.negate(), RealScalar.ONE);
    assertEquals(RealScalar.of(2), err);
  }

  public void testMixed2() {
    Scalar err = So2Metric.INSTANCE.distance(RealScalar.ONE, Pi.TWO.subtract(RealScalar.ONE));
    assertEquals(RealScalar.of(2), err);
  }

  public void testMultiples() {
    Scalar err = So2Metric.INSTANCE.distance(Pi.VALUE.multiply(RealScalar.of(6)), RealScalar.ONE);
    assertEquals(RealScalar.ONE, err);
  }
}
