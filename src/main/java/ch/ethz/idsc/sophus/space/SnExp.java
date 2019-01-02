// code by jph
package ch.ethz.idsc.sophus.space;

import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sinc;

/** implementation is based on the function "strans" taken from
 * "Freeform Curves on Spheres of Arbitrary Dimension"
 * by Scott Schaefer and Ron Goldman, page 5 */
public class SnExp implements LieExponential {
  private final Tensor point;

  public SnExp(Tensor point) {
    this.point = VectorQ.require(point);
    if (point.length() < 2)
      throw TensorRuntimeException.of(point);
  }

  @Override // from LieExponential
  public Tensor exp(Tensor x) {
    // x is orthogonal to base point
    Scalar norm = Norm._2.ofVector(x);
    Scalar sinc = Sinc.FUNCTION.apply(norm);
    return point.multiply(Cos.FUNCTION.apply(norm)).add(x.multiply(sinc));
  }

  @Override
  public Tensor log(Tensor g) {
    return null;
  }
}
