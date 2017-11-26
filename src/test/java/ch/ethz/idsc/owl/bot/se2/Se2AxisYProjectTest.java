// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.sample.CircleRandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2AxisYProjectTest extends TestCase {
  public void testEx1() {
    Scalar t = Se2AxisYProject.of(Tensors.vector(1, 0, 0.3), Tensors.vector(10, 0));
    assertTrue(Chop._12.close(t, RealScalar.of(4.163485907994182)));
  }

  public void testEx2() {
    Scalar t = Se2AxisYProject.of(Tensors.vector(1, 0, 0.3), Tensors.vector(10, 3));
    assertTrue(Chop._12.close(t, RealScalar.of(5.124917769722165)));
  }

  public void testEx3() {
    Scalar t = Se2AxisYProject.of(Tensors.vector(1, 0, 0.0), Tensors.vector(10, 3));
    assertTrue(Chop._12.close(t, RealScalar.of(10)));
  }

  public void testEx4() {
    Scalar t = Se2AxisYProject.of(Tensors.vector(2, 0, 0.0), Tensors.vector(10, 3));
    assertTrue(Chop._12.close(t, RealScalar.of(5)));
  }

  public void testCheck() {
    RandomSampleInterface rsi = new CircleRandomSample(Tensors.vector(0, 0), RealScalar.of(10));
    for (int index = 0; index < 100; ++index) {
      Tensor u = Tensors.vector(0.9, 0, 0.3);
      Tensor p = RandomSample.of(rsi);
      Scalar t = Se2AxisYProject.of(u, p).negate();
      Tensor m = Se2Utils.toSE2Matrix(Se2Utils.integrate_g0(u.multiply(t)));
      Tensor v = m.dot(p.copy().append(RealScalar.ONE));
      assertTrue(Chop._12.allZero(v.Get(0)));
    }
  }

  public void testCheck2() {
    RandomSampleInterface rsi = new CircleRandomSample(Tensors.vector(0, 0), RealScalar.of(10));
    for (int index = 0; index < 100; ++index) {
      Tensor u = Tensors.vector(1.1, 0, 1.3);
      Tensor p = RandomSample.of(rsi);
      Scalar t = Se2AxisYProject.of(u, p).negate();
      Tensor m = Se2Utils.toSE2Matrix(Se2Utils.integrate_g0(u.multiply(t)));
      Tensor v = m.dot(p.copy().append(RealScalar.ONE));
      assertTrue(Chop._12.allZero(v.Get(0)));
    }
  }

  public void testCheckZero() {
    RandomSampleInterface rsi = new CircleRandomSample(Tensors.vector(0, 0), RealScalar.of(10));
    for (int index = 0; index < 100; ++index) {
      Tensor u = Tensors.vector(2, 0, 0);
      Tensor p = RandomSample.of(rsi);
      Scalar t = Se2AxisYProject.of(u, p).negate();
      Tensor m = Se2Utils.toSE2Matrix(Se2Utils.integrate_g0(u.multiply(t)));
      Tensor v = m.dot(p.copy().append(RealScalar.ONE));
      assertTrue(Chop._12.allZero(v.Get(0)));
    }
  }
}
