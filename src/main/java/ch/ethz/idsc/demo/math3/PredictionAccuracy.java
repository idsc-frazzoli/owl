// code by jph
package ch.ethz.idsc.demo.math3;

import org.apache.commons.math3.analysis.MultivariateFunction;

import ch.ethz.idsc.sophus.lie.BiinvariantMeans;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ class PredictionAccuracy implements MultivariateFunction {
  private final Tensor[] data;
  private final Se2GroupElement[] inverse;
  private final int len;

  /** @param pqr_t with dimensions [n, m + 1, 3] */
  public PredictionAccuracy(Tensor pqr_t) {
    len = pqr_t.get(0).length() - 1;
    data = pqr_t.stream().map(row -> row.extract(0, len)).toArray(Tensor[]::new);
    inverse = pqr_t.stream() //
        .map(row -> row.get(len)) //
        .map(Se2Group.INSTANCE::element) //
        .map(Se2GroupElement::inverse) //
        .toArray(Se2GroupElement[]::new);
  }

  @Override // from MultivariateFunction
  public double value(double[] point) {
    Tensor weights = AffineAppend.of(Tensors.vectorDouble(point));
    TensorUnaryOperator tensorUnaryOperator = BiinvariantMeans.of(Se2BiinvariantMean.LINEAR, weights);
    Scalar sum = RealScalar.ZERO;
    for (int index = 0; index < data.length; ++index) {
      Tensor pqr = data[index];
      Tensor t_prediction = tensorUnaryOperator.apply(pqr);
      Tensor g = inverse[index].combine(t_prediction);
      Scalar err = Norm._2.ofVector(Se2CoveringExponential.INSTANCE.log(g));
      sum = sum.add(err);
    }
    return sum.number().doubleValue();
  }
}
