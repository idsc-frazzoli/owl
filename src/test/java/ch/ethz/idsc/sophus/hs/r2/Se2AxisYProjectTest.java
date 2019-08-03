// code by jph
package ch.ethz.idsc.sophus.hs.r2;

import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.sample.RandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.owl.math.sample.SphereRandomSample;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.QuantityUnit;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2AxisYProjectTest extends TestCase {
  public void testEx1() {
    Scalar t = Se2AxisYProject.of(Tensors.vector(1, 0, 0.3)).apply(Tensors.vector(10, 0));
    assertTrue(Chop._12.close(t, RealScalar.of(4.163485907994182)));
  }

  public void testEx2() {
    Scalar t = Se2AxisYProject.of(Tensors.vector(1, 0, 0.3)).apply(Tensors.vector(10, 3));
    assertTrue(Chop._12.close(t, RealScalar.of(5.124917769722165)));
  }

  public void testEx2NegU() {
    double speed = -1;
    Tensor u = Tensors.vector(1 * speed, 0, 0.3 * speed);
    Tensor p = Tensors.vector(-10, 3);
    Scalar t = Se2AxisYProject.of(u).apply(p);
    assertTrue(Chop._12.close(t, RealScalar.of(5.124917769722165)));
    TensorUnaryOperator se2ForwardAction = //
        new Se2Bijection(Se2CoveringExponential.INSTANCE.exp(u.multiply(t.negate()))).forward();
    Tensor v = se2ForwardAction.apply(p);
    assertTrue(Chop._13.close(v, Tensors.fromString("{0, -6.672220679869088}")));
  }

  public void testEx2Neg() {
    Tensor u = Tensors.vector(1, 0, 0.3);
    Tensor p = Tensors.vector(-10, 3);
    Scalar t = Se2AxisYProject.of(u).apply(p);
    assertTrue(Chop._12.close(t, RealScalar.of(-5.124917769722165)));
    TensorUnaryOperator se2ForwardAction = //
        new Se2Bijection(Se2CoveringExponential.INSTANCE.exp(u.multiply(t.negate()))).forward();
    Tensor v = se2ForwardAction.apply(p);
    assertTrue(Chop._13.close(v, Tensors.fromString("{0, -6.672220679869088}")));
  }

  public void testEx3() {
    Scalar t = Se2AxisYProject.of(Tensors.vector(1, 0, 0.0)).apply(Tensors.vector(10, 3));
    assertTrue(Chop._12.close(t, RealScalar.of(10)));
  }

  public void testEx4() {
    Scalar t = Se2AxisYProject.of(Tensors.vector(2, 0, 0.0)).apply(Tensors.vector(10, 3));
    assertTrue(Chop._12.close(t, RealScalar.of(5)));
  }

  public void testEx4NegU() {
    Scalar t = Se2AxisYProject.of(Tensors.vector(-2, 0, 0.0)).apply(Tensors.vector(10, 3));
    assertTrue(Chop._12.close(t, RealScalar.of(-5)));
  }

  public void testEx4Neg() {
    Tensor u = Tensors.vector(2, 0, 0);
    Tensor p = Tensors.vector(-10, 3);
    Scalar t = Se2AxisYProject.of(u).apply(p);
    assertTrue(Chop._12.close(t, RealScalar.of(-5)));
    TensorUnaryOperator se2ForwardAction = //
        new Se2Bijection(Se2CoveringExponential.INSTANCE.exp(u.multiply(t.negate()))).forward();
    Tensor v = se2ForwardAction.apply(p);
    assertEquals(v, Tensors.vector(0, 3));
  }

  public void testEps1() {
    Tensor u = Tensors.vector(2, 0, Double.MIN_VALUE);
    Scalar t = Se2AxisYProject.of(u).apply(Tensors.vector(10, 3));
    assertFalse(Scalars.isZero(u.Get(2)));
    assertTrue(Chop._12.close(t, RealScalar.of(5)));
  }

  public void testEps2() {
    Tensor u = Tensors.vector(2, 0, -Double.MIN_VALUE);
    Scalar t = Se2AxisYProject.of(u).apply(Tensors.vector(10, 3));
    assertFalse(Scalars.isZero(u.Get(2)));
    assertTrue(Chop._12.close(t, RealScalar.of(5)));
  }

  public void testZeroSpeedNonZeroPos() {
    Tensor u = Tensors.vector(0, 0, 0);
    Scalar t = Se2AxisYProject.of(u).apply(Tensors.vector(10, 3));
    assertEquals(t, DoubleScalar.POSITIVE_INFINITY);
  }

  public void testZeroSpeedNonZeroPos2() {
    Tensor u = Tensors.vector(0, 0, 0);
    Scalar t = Se2AxisYProject.of(u).apply(Tensors.vector(-10, 3));
    assertEquals(t, DoubleScalar.POSITIVE_INFINITY.negate());
  }

  public void testZeroSpeedZeroPos() {
    Tensor u = Tensors.vector(0, 0, 0);
    Scalar t = Se2AxisYProject.of(u).apply(Tensors.vector(0, 3));
    assertEquals(t, RealScalar.ZERO);
  }

  public void testCheck() {
    RandomSampleInterface rsi = SphereRandomSample.of(Tensors.vector(0, 0), RealScalar.of(10));
    for (int index = 0; index < 100; ++index) {
      Tensor u = Tensors.vector(0.9, 0, 0.3);
      Tensor p = RandomSample.of(rsi);
      Scalar t = Se2AxisYProject.of(u).apply(p).negate();
      Tensor m = Se2Matrix.of(Se2CoveringExponential.INSTANCE.exp(u.multiply(t)));
      Tensor v = m.dot(p.copy().append(RealScalar.ONE));
      assertTrue(Chop._12.allZero(v.Get(0)));
    }
  }

  public void testCheck2() {
    RandomSampleInterface rsi = SphereRandomSample.of(Tensors.vector(0, 0), RealScalar.of(10));
    for (int index = 0; index < 100; ++index) {
      Tensor u = Tensors.vector(1.1, 0, 1.3);
      Tensor p = RandomSample.of(rsi);
      Scalar t = Se2AxisYProject.of(u).apply(p).negate();
      Tensor m = Se2Matrix.of(Se2CoveringExponential.INSTANCE.exp(u.multiply(t)));
      Tensor v = m.dot(p.copy().append(RealScalar.ONE));
      assertTrue(Chop._12.allZero(v.Get(0)));
    }
  }

  public void testCheckZero() {
    RandomSampleInterface rsi = SphereRandomSample.of(Tensors.vector(0, 0), RealScalar.of(10));
    for (int index = 0; index < 100; ++index) {
      Tensor u = Tensors.vector(2, 0, 0);
      Tensor p = RandomSample.of(rsi);
      Scalar t = Se2AxisYProject.of(u).apply(p).negate();
      Tensor m = Se2Matrix.of(Se2CoveringExponential.INSTANCE.exp(u.multiply(t)));
      Tensor v = m.dot(p.copy().append(RealScalar.ONE));
      assertTrue(Chop._12.allZero(v.Get(0)));
    }
  }

  public void testUnitsNormal() {
    // [m*s^-1], 0.0, (rate*speed)[rad*s^-1]
    Tensor u = Tensors.fromString("{1.1[m*s^-1], 0, 1.3[s^-1]}"); // SI
    Tensor p = Tensors.fromString("{2.1[m], 0.7[m]}");
    Scalar t = Se2AxisYProject.of(u).apply(p);
    Scalar magnitude = QuantityMagnitude.SI().in("s").apply(t);
    assertTrue(Chop._10.close(magnitude, DoubleScalar.of(1.154854847741819)));
    assertEquals(QuantityUnit.of(t), Unit.of("s"));
    assertTrue(Scalars.nonZero(t));
  }

  public void testUnitsBeZero() {
    // [m*s^-1], 0.0, (rate*speed)[rad*s^-1]
    Tensor u = Tensors.fromString("{1.1[m*s^-1], 0, 0[s^-1]}"); // SI
    Tensor p = Tensors.fromString("{2.1[m], 0.7[m]}");
    Scalar t = Se2AxisYProject.of(u).apply(p);
    assertEquals(QuantityUnit.of(t), Unit.of("s"));
    assertTrue(Scalars.nonZero(t));
  }

  public void testUnitsBeZeroVxZero1() {
    // [m*s^-1], 0.0, (rate*speed)[rad*s^-1]
    Tensor u = Tensors.fromString("{0.0[m*s^-1], 0, 0[s^-1]}"); // SI
    Tensor p = Tensors.fromString("{2.1[m], 0.7[m]}");
    Scalar t = Se2AxisYProject.of(u).apply(p);
    assertEquals(QuantityUnit.of(t), Unit.of("s"));
    assertTrue(Scalars.nonZero(t));
  }

  public void testUnitsBeZeroVxZero2() {
    // [m*s^-1], 0.0, (rate*speed)[rad*s^-1]
    Tensor u = Tensors.fromString("{0.0[m*s^-1], 0, 0[s^-1]}"); // SI
    Tensor p = Tensors.fromString("{0.0[m], 0.7[m]}");
    Scalar t = Se2AxisYProject.of(u).apply(p);
    assertEquals(QuantityUnit.of(t), Unit.of("s"));
    assertTrue(Scalars.isZero(t));
  }
}
