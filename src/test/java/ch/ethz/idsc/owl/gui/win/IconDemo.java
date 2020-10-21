// code by jph
package ch.ethz.idsc.owl.gui.win;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.mat.GaussianMatrix;

/* package */ enum IconDemo {
  ;
  public static void main(String[] args) throws Exception {
    for (ColorDataGradients colorDataFunction : ColorDataGradients.values()) {
      Tensor matrix = GaussianMatrix.of(11);
      matrix = matrix.map(scalar -> Scalars.lessThan(RealScalar.of(0.001), scalar) ? scalar : DoubleScalar.INDETERMINATE);
      Tensor image = ArrayPlot.of(matrix, colorDataFunction);
      System.out.println(Dimensions.of(image));
      Export.of(HomeDirectory.Pictures(colorDataFunction.name() + ".png"), image);
    }
  }
}
