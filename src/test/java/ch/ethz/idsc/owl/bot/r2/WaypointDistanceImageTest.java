// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.sophus.curve.BSpline1CurveSubdivision;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.TestCase;

public class WaypointDistanceImageTest extends TestCase {
  public void testSimple() {
    Tensor waypoints = ResourceData.of("/dubilab/waypoints/20180425.csv");
    waypoints = new BSpline1CurveSubdivision(Se2Geodesic.INSTANCE).cyclic(waypoints);
    BufferedImage bufferedImage = //
        WaypointDistanceImage.linear(waypoints, Tensors.vector(100, 100), 5, new Dimension(640, 640));
    Tensor tensor = ImageFormat.from(bufferedImage);
    List<Tensor> list = tensor.flatten(-1).distinct().collect(Collectors.toList());
    assertEquals(list.get(0), RealScalar.ONE);
    assertEquals(list.get(1), RealScalar.ZERO);
  }
}
