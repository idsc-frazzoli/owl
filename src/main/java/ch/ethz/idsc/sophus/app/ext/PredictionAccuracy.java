// code by jph
package ch.ethz.idsc.sophus.app.ext;

import org.apache.commons.math3.analysis.MultivariateFunction;

import ch.ethz.idsc.sophus.lie.BiinvariantMeans;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMean;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ class PredictionAccuracy implements MultivariateFunction {
  private final Tensor pqr_t;
  private final int m;

  /** @param pqr_t with dimensions [n, m + 1, 3] */
  public PredictionAccuracy(Tensor pqr_t) {
    this.pqr_t = pqr_t;
    m = pqr_t.get(0).length() - 1;
  }

  @Override // from MultivariateFunction
  public double value(double[] point) {
    Tensor weights = MakeAffine.of(Tensors.vectorDouble(point));
    TensorUnaryOperator tensorUnaryOperator = BiinvariantMeans.of(Se2BiinvariantMean.FILTER, weights);
    Scalar sum = RealScalar.ZERO;
    for (Tensor sequence : pqr_t) {
      Tensor pqr = sequence.extract(0, m);
      Tensor t_prediction = tensorUnaryOperator.apply(pqr);
      Tensor t_measured = sequence.get(m);
      Scalar err = Norm._2.between(t_prediction, t_measured);
      sum = sum.add(err);
    }
    return sum.number().doubleValue();
  }
}
