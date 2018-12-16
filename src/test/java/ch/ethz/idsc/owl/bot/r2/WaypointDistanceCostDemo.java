// code by ynager
package ch.ethz.idsc.owl.bot.r2;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.sophus.curve.BSpline1CurveSubdivision;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;

enum WaypointDistanceCostDemo {
  ;
  public static void main(String[] args) {
    Tensor waypoints = Objects.requireNonNull(ResourceData.of("/dubilab/waypoints/20180610.csv"));
    waypoints = new BSpline1CurveSubdivision(Se2Geodesic.INSTANCE).cyclic(waypoints);
    ImageCostFunction imageCostFunction = //
        WaypointDistanceCost.linear(waypoints, Tensors.vector(85.33, 85.33), 10.0f, new Dimension(440, 640));
    Tensor image = ArrayPlot.of(imageCostFunction.image(), ColorDataGradients.CLASSIC);
    BufferedImage bufferedImage = ImageFormat.of(image);
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
