// code by jph
package ch.ethz.idsc.owl.math.lane;

import java.io.IOException;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoids;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class StableLaneTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    LaneInterface laneInterface = Serialization.copy(StableLanes.of( //
        Tensors.fromString("{{0[m], 1[m], 2}, {2[m], 0[m], 4}, {-1[m],-3[m], -2}}"), //
        LaneRiesenfeldCurveSubdivision.of(Clothoids.INSTANCE, 1)::cyclic, 3, Quantity.of(1, "m")));
    assertEquals(laneInterface.controlPoints().length(), 3);
    MatrixQ.require(laneInterface.midLane());
    MatrixQ.require(laneInterface.leftBoundary());
    MatrixQ.require(laneInterface.rightBoundary());
    VectorQ.require(laneInterface.margins());
  }

  public void testStraight() throws ClassNotFoundException, IOException {
    LaneInterface laneInterface = Serialization.copy(StableLanes.of( //
        Tensors.fromString("{{0[m], 0[m], 0}, {2[m], 0[m], 0}}"), //
        LaneRiesenfeldCurveSubdivision.of(Clothoids.INSTANCE, 1)::string, 3, Quantity.of(0.5, "m")));
    assertEquals(laneInterface.margins().get(0), Quantity.of(0.5, "m"));
    {
      Tensor leftBoundary = MatrixQ.require(laneInterface.leftBoundary());
      assertEquals(leftBoundary.get(Tensor.ALL, 1), laneInterface.margins());
      assertTrue(Chop._12.allZero(leftBoundary.get(Tensor.ALL, 2)));
    }
    {
      Tensor rightBoundary = MatrixQ.require(laneInterface.rightBoundary());
      assertEquals(rightBoundary.get(Tensor.ALL, 1), laneInterface.margins().negate());
      assertTrue(Chop._12.allZero(rightBoundary.get(Tensor.ALL, 2)));
    }
  }
}
