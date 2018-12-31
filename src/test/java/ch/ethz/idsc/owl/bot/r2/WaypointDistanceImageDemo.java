// code by ynager
package ch.ethz.idsc.owl.bot.r2;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.Objects;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.sophus.curve.BSpline1CurveSubdivision;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;

/* package */ enum WaypointDistanceImageDemo {
  ;
  public static void main(String[] args) {
    Tensor waypoints = Objects.requireNonNull(ResourceData.of("/dubilab/waypoints/20180610.csv"));
    waypoints = new BSpline1CurveSubdivision(Se2Geodesic.INSTANCE).cyclic(waypoints);
    BufferedImage bufferedImage = new WaypointDistanceImage( //
        waypoints, true, RealScalar.ONE, RealScalar.of(7.5), new Dimension(600, 600)).bufferedImage();
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    byte[] bytes = dataBufferByte.getData();
    for (int index = 0; index < bytes.length; ++index)
      if (bytes[index] != 0)
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
}
