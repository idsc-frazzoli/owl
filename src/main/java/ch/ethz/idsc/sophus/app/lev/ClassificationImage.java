// code by jph
package ch.ethz.idsc.sophus.app.lev;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum ClassificationImage {
  BLENDED {
    @Override
    public TensorUnaryOperator operator(Classification classification, TensorUnaryOperator operator, ColorDataIndexed colorDataIndexed) {
      return point -> {
        ClassificationResult classificationResult = classification.result(operator.apply(point));
        Tensor rgba = colorDataIndexed.apply(RealScalar.of(classificationResult.getLabel()));
        rgba.set(classificationResult.getConfidence().multiply(RealScalar.of(128 + 64)), 3);
        return rgba;
      };
    }
  }, //
  LABEL {
    @Override
    public TensorUnaryOperator operator(Classification classification, TensorUnaryOperator operator, ColorDataIndexed colorDataIndexed) {
      ColorDataIndexed _colorDataIndexed = colorDataIndexed.deriveWithAlpha(128 + 64);
      return point -> _colorDataIndexed.apply(RealScalar.of(classification.result(operator.apply(point)).getLabel()));
    }
  }, //
  CONFIDENCE {
    @Override
    public TensorUnaryOperator operator(Classification classification, TensorUnaryOperator operator, ColorDataIndexed colorDataIndexed) {
      return point -> ColorDataGradients.CLASSIC.apply(classification.result(operator.apply(point)).getConfidence());
    }
  }, //
  ;

  /** @param classification
   * @param operator that maps a point to distances/weights/coordinates
   * @param colorDataIndexed
   * @return */
  public abstract TensorUnaryOperator operator( //
      Classification classification, TensorUnaryOperator operator, ColorDataIndexed colorDataIndexed);
}
