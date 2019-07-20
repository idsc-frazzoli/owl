// code by jph
package ch.ethz.idsc.sophus.hs.s3;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quaternion;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;
import junit.framework.TestCase;

public class UnitQuaternionDistanceTest extends TestCase {
  public void testSimple() {
    Quaternion p = Quaternion.of(3, 1, 2, 3);
    p = p.divide(p.abs());
    Quaternion q = Quaternion.of(-2, 0, -4, 7);
    q = q.divide(q.abs());
    Chop._14.requireClose(p.abs(), RealScalar.ONE);
    Chop._14.requireClose(q.abs(), RealScalar.ONE);
    Scalar d1 = UnitQuaternionDistance.INSTANCE.distance(p, q);
    Scalar dq = UnitQuaternionDistance.INSTANCE.distance(p.multiply(q), q.multiply(q));
    Scalar dp = UnitQuaternionDistance.INSTANCE.distance(p.multiply(p), p.multiply(q));
    Chop._14.requireClose(d1, dp);
    Chop._14.requireClose(d1, dq);
    assertTrue(Chop._14.allZero(UnitQuaternionDistance.INSTANCE.distance(p, p)));
    Scalar distance = LogUnitQuaternionDistance.INSTANCE.distance(p, q);
    Chop._14.requireClose(dp, distance);
    Chop._14.requireClose(dq, distance);
  }

  public void testEichen() {
    Quaternion p0 = Quaternion.of(1, 0, 0, 0);
    Quaternion p1 = Quaternion.of(0, 1, 0, 0);
    Quaternion p2 = Quaternion.of(0, 0, 1, 0);
    Quaternion p3 = Quaternion.of(0, 0, 0, 1);
    Scalar d01a = UnitQuaternionDistance.INSTANCE.distance(p0, p1);
    Chop._14.requireClose(d01a, Pi.HALF);
    Scalar d01b = LogUnitQuaternionDistance.INSTANCE.distance(p0, p1);
    Chop._14.requireClose(d01a, d01b);
    Scalar d23a = UnitQuaternionDistance.INSTANCE.distance(p2, p3);
    Scalar d23b = LogUnitQuaternionDistance.INSTANCE.distance(p2, p3);
    Chop._14.requireClose(d23a, d23b);
    assertEquals(p0.abs(), RealScalar.ONE);
    assertEquals(p1.abs(), RealScalar.ONE);
    assertEquals(p2.abs(), RealScalar.ONE);
    assertEquals(p3.abs(), RealScalar.ONE);
  }

  public void testQuaternionLogExp() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 30; ++index) {
      Quaternion quaternion = Quaternion.of(RandomVariate.of(distribution), RandomVariate.of(distribution, 3));
      Quaternion log = Log.of(quaternion);
      Quaternion exp = Exp.of(log);
      Chop._14.requireClose(quaternion, exp);
    }
  }

  public void testQuaternionExpLog() {
    Distribution distribution = NormalDistribution.of(0, .3);
    for (int index = 0; index < 30; ++index) {
      Quaternion quaternion = Quaternion.of(RandomVariate.of(distribution), RandomVariate.of(distribution, 3));
      Quaternion exp = Exp.of(quaternion);
      Quaternion log = Log.of(exp);
      Chop._14.requireClose(quaternion, log);
    }
  }
}
