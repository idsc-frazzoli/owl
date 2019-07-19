// code by jph
package ch.ethz.idsc.sophus.hs.s3;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
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
    Chop._12.requireClose(p.abs(), RealScalar.ONE);
    Chop._12.requireClose(q.abs(), RealScalar.ONE);
    Scalar d1 = UnitQuaternionDistance.INSTANCE.distance(p, q);
    Scalar dq = UnitQuaternionDistance.INSTANCE.distance(p.multiply(q), q.multiply(q));
    Scalar dp = UnitQuaternionDistance.INSTANCE.distance(p.multiply(p), p.multiply(q));
    Chop._12.requireClose(d1, dp);
    Chop._12.requireClose(d1, dq);
    assertTrue(Chop._12.allZero(UnitQuaternionDistance.INSTANCE.distance(p, p)));
    // Scalar d2 =
    LogUnitQuaternionDistance.INSTANCE.distance(p, q);
    // System.out.println(d1);
    // System.out.println(d2.multiply(RealScalar.of(2)));
  }

  public void testQuaternionLogExp() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 100; ++index) {
      Quaternion quaternion = Quaternion.of(RandomVariate.of(distribution), RandomVariate.of(distribution, 3));
      Quaternion log = Log.of(quaternion);
      Quaternion exp = Exp.of(log);
      Chop._12.requireClose(quaternion, exp);
    }
  }

  public void testQuaternionExpLog() {
    Distribution distribution = NormalDistribution.of(0, .3);
    for (int index = 0; index < 100; ++index) {
      Quaternion quaternion = Quaternion.of(RandomVariate.of(distribution), RandomVariate.of(distribution, 3));
      Quaternion exp = Exp.of(quaternion);
      Quaternion log = Log.of(exp);
      Chop._12.requireClose(quaternion, log);
    }
  }
}
