// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.awt.Dimension;

import ch.ethz.idsc.owl.subdiv.curve.BSpline1CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.Se2Geodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.TestCase;

public class WaypointDistanceImageTest extends TestCase {
  public void testSimple() {
    Tensor waypoints = ResourceData.of("/dubilab/waypoints/20180425.csv");
    waypoints = new BSpline1CurveSubdivision(Se2Geodesic.INSTANCE).cyclic(waypoints);
    WaypointDistanceImage.linear(waypoints, Tensors.vector(100, 100), 5, new Dimension(640, 640));
  }
}
