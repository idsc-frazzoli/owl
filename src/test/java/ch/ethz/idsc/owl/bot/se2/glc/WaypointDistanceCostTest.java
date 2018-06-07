package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.TestCase;

public class WaypointDistanceCostTest extends TestCase {
  public void testSimple() {
    Tensor waypoints = ResourceData.of("/demo/dubendorf/hangar/20180425waypoints.csv");
    WaypointDistanceCost wdc = new WaypointDistanceCost(waypoints, Tensors.vector(85.33, 85.33), 10.0f);
    for (int i = 0; i < waypoints.length(); i++) {
      assertEquals(wdc.pointcost(waypoints.get(i)), RealScalar.ZERO);
    }
    assertEquals(wdc.pointcost(Tensors.vector(10, 10)), RealScalar.ONE);
  }
  // public void testImage() {
  // JFrame frame = new JFrame() {
  // @Override
  // public void paint(Graphics g) {
  // Tensor waypoints = ResourceData.of("/demo/dubendorf/hangar/20180425waypoints.csv");
  // WaypointDistanceCost wdc = new WaypointDistanceCost(waypoints, Tensors.vector(85.33, 85.33), 10.0f);
  // g.drawImage(ImageFormat.of(wdc.image), 0, 0, null);
  // }
  // };
  // frame.setSize(640, 640);
  // frame.setLocation(100, 100);
  // frame.setVisible(true);
  // }
}
