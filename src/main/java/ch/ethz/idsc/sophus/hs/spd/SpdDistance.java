// code by jph
package ch.ethz.idsc.sophus.hs.spd;

import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.sca.AbsSquared;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** Reference:
 * "Riemannian Geometric Statistics in Medical Image Analysis", 2020
 * Edited by Pennec, Sommer, Fletcher, p. 82 */
public enum SpdDistance implements TensorMetric {
  INSTANCE;
  // ---
  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    SpdSqrt spdSplit = new SpdSqrt(p);
    Tensor pn12 = spdSplit.inverse();
    Tensor matrix = pn12.dot(q).dot(pn12);
    return n(Transpose.of(matrix).add(matrix).multiply(RationalScalar.HALF));
  }

  /** @param matrix spd
   * @return */
  static Scalar n(Tensor matrix) {
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    return Sqrt.FUNCTION.apply(eigensystem.values().stream() //
        .map(Scalar.class::cast) //
        .map(Log.FUNCTION) //
        .map(AbsSquared.FUNCTION) //
        .reduce(Scalar::add) //
        .get());
  }
}
