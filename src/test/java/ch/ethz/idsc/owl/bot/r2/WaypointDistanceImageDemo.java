// code by ynager
package ch.ethz.idsc.owl.bot.r2;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.ref.d1.BSpline1CurveSubdivision;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;

/* package */ enum WaypointDistanceImageDemo {
  ;
  public static void show(Tensor waypoints) {
    waypoints = new BSpline1CurveSubdivision(Se2Geodesic.INSTANCE).cyclic(waypoints);
    WaypointDistanceImage waypointDistanceImage = new WaypointDistanceImage( //
        waypoints, true, RealScalar.ONE, RealScalar.of(7.5), new Dimension(600, 600));
    BufferedImage bufferedImage = waypointDistanceImage.bufferedImage();
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    byte[] bytes = dataBufferByte.getData();
    for (int index = 0; index < bytes.length; ++index)
      if (bytes[index] != 0) // promote non-black to white
        bytes[index] = -1;
    JFrame frame = new JFrame() {
      @Override
      public void paint(Graphics graphics) {
        graphics.drawImage(bufferedImage, 0, 0, null);
      }
    };
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setSize(700, 700);
    frame.setLocation(100, 100);
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    show(ResourceData.of("/dubilab/waypoints/20180610.csv"));
    show(ResourceData.of("/dubilab/waypoints/20181126.csv"));
  }
}
