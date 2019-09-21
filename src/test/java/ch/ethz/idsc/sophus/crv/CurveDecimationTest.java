// code by jph
package ch.ethz.idsc.sophus.crv;

import java.io.IOException;

import ch.ethz.idsc.sophus.app.io.GokartPoseData;
import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.subare.util.RandomChoice;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class CurveDecimationTest extends TestCase {
  public void testReverse() throws ClassNotFoundException, IOException {
    GokartPoseData gokartPoseData = GokartPoseDataV2.RACING_DAY;
    CurveDecimation curveDecimation = Serialization.copy( //
        CurveDecimation.symmetric(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE::log, RealScalar.of(0.3)));
    String name = RandomChoice.of(gokartPoseData.list());
    Tensor matrix = gokartPoseData.getPose(name, 2000);
    Tensor t1 = Reverse.of(curveDecimation.apply(matrix));
    Tensor t2 = curveDecimation.apply(Reverse.of(matrix));
    assertTrue(t1.length() < 100);
    assertTrue(t2.length() < 100);
    assertEquals(t1, t2);
  }

  public void testReverse2() throws ClassNotFoundException, IOException {
    GokartPoseData gokartPoseData = GokartPoseDataV2.RACING_DAY;
    CurveDecimation curveDecimation = Serialization.copy( //
        CurveDecimation.projected(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, RealScalar.of(0.3)));
    String name = RandomChoice.of(gokartPoseData.list());
    Tensor matrix = gokartPoseData.getPose(name, 2000);
    Tensor t1 = Reverse.of(curveDecimation.apply(matrix));
    Tensor t2 = curveDecimation.apply(Reverse.of(matrix));
    System.out.println(t1.length());
    System.out.println(t2.length());
    // assertTrue(t1.length() < 100);
    // assertTrue(t2.length() < 100);
    // assertEquals(t1, t2);
  }
}
