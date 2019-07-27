// code by jph
package ch.ethz.idsc.sophus.itp;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.pdf.EqualizingDistribution;
import ch.ethz.idsc.tensor.pdf.InverseCDF;

/** function defined on the interval [0, 1] */
public class ArcLengthParameterization implements ScalarTensorFunction {
  /** @param distances vector with non-negative entries
   * @param splitInterface non-null
   * @param tensor with length one more than length of given differences
   * @return */
  public static ScalarTensorFunction of(Tensor distances, SplitInterface splitInterface, Tensor tensor) {
    if (distances.length() + 1 == tensor.length())
      return new ArcLengthParameterization(distances, splitInterface, tensor);
    throw TensorRuntimeException.of(distances, tensor);
  }

  // ---
  private final InverseCDF inverseCDF;
  private final Interpolation interpolation;

  private ArcLengthParameterization(Tensor distances, SplitInterface splitInterface, Tensor tensor) {
    inverseCDF = (InverseCDF) EqualizingDistribution.fromUnscaledPDF(distances);
    interpolation = GeodesicInterpolation.of(splitInterface, tensor);
  }

  @Override
  public Tensor apply(Scalar scalar) {
    return interpolation.at(inverseCDF.quantile(scalar));
  }
}
