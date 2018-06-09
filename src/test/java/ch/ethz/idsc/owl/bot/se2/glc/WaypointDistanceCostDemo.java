// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Graphics;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;

enum WaypointDistanceCostDemo {
  ;
  public static void main(String[] args) {
    Tensor waypoints = ResourceData.of("/demo/dubendorf/hangar/20180425waypoints.csv");
    WaypointDistanceCost wdc = new WaypointDistanceCost(waypoints, Tensors.vector(85.33, 85.33), 10.0f, Arrays.asList(440, 640));
    JFrame frame = new JFrame() {
      @Override
      public void paint(Graphics graphics) {
        // graphics.drawImage(wdc.visualization(), 0, 0, null);
        graphics.drawImage(ImageFormat.of(wdc.image().multiply(RealScalar.of(100.))), 0, 0, null);
      }
    };
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setSize(700, 700);
    frame.setLocation(100, 100);
    frame.setVisible(true);
  }
}
