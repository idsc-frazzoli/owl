package ch.ethz.idsc.owl.gui;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.opt.GaussianMatrix;

enum IconDemo {
  ;
  public static void main(String[] args) throws Exception {
    for (ColorDataGradients colorDataFunction : ColorDataGradients.values()) {
      Tensor matrix = GaussianMatrix.of(11);
      // System.out.println(matrix.Get(0, 0));
      // System.out.println(matrix.Get(7, 7));
      matrix = matrix.map(scalar -> Scalars.lessThan(RealScalar.of(0.001), scalar) ? scalar : DoubleScalar.INDETERMINATE);
      Tensor image = ArrayPlot.of(matrix, colorDataFunction);
      System.out.println(Dimensions.of(image));
      Export.of(UserHome.Pictures(colorDataFunction.name() + ".png"), image);
      // BufferedImage bufferedImage = ImageFormat.of(a);
      // JLabel jLabel = new JLabel();
      // jLabel.setIcon(new ImageIcon());
    }
  }
}
