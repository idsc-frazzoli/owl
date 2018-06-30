// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.awt.Dimension;

import ch.ethz.idsc.owl.subdiv.curve.BSpline1CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.Se2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.TestCase;

public class WaypointDistanceImagesTest extends TestCase {
  public void testSimple() {
    Tensor waypoints = ResourceData.of("/dubilab/waypoints/20180425.csv");
    waypoints = new BSpline1CurveSubdivision(Se2Geodesic.INSTANCE).apply(waypoints);
    WaypointDistanceImage.linear(waypoints, RealScalar.of(100), 5, new Dimension(640, 640));
  }
}
