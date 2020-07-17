// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.sophus.app.api.GeodesicArrayPlot;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ enum StaticHelper {
  ;
  public static BufferedImage computeImage( //
      GeodesicArrayPlot geodesicArrayPlot, Classification classification, //
      TensorUnaryOperator operator, int res, ColorDataLists colorDataLists) {
    ColorDataIndexed colorDataIndexed = colorDataLists.cyclic().deriveWithAlpha(128 + 64);
    TensorUnaryOperator tensorUnaryOperator = //
        point -> colorDataIndexed.apply(RealScalar.of(classification.result(operator.apply(point)).getLabel()));
    return ImageFormat.of(geodesicArrayPlot.array(res, tensorUnaryOperator, null));
  }

  public static BufferedImage computeImage2( //
      GeodesicArrayPlot geodesicArrayPlot, Classification classification, //
      TensorUnaryOperator operator, int res, ColorDataLists colorDataLists) {
    TensorUnaryOperator tensorUnaryOperator = //
        point -> ColorDataGradients.CLASSIC.apply(classification.result(operator.apply(point)).getConfidence());
    return ImageFormat.of(geodesicArrayPlot.array(res, tensorUnaryOperator, null));
  }

  public static BufferedImage computeImage3( //
      GeodesicArrayPlot geodesicArrayPlot, Classification classification, //
      TensorUnaryOperator operator, int res, ColorDataLists colorDataLists) {
    ColorDataIndexed colorDataIndexed = colorDataLists.strict();
    TensorUnaryOperator tensorScalarFunction = //
        point -> {
          ClassificationResult classificationResult = classification.result(operator.apply(point));
          Tensor rgba = colorDataIndexed.apply(RealScalar.of(classificationResult.getLabel()));
          rgba.set(classificationResult.getConfidence().multiply(RealScalar.of(128 + 64)), 3);
          return rgba;
        };
    return ImageFormat.of(geodesicArrayPlot.array(res, tensorScalarFunction, Tensors.vector(0, 0, 0, 0)));
  }
}
