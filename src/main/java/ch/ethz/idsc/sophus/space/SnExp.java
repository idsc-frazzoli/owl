// code by jph
package ch.ethz.idsc.sophus.space;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sinc;

/** implementation is based on the function "strans" taken from
 * "Freeform Curves on Spheres of Arbitrary Dimension"
 * by Scott Schaefer and Ron Goldman, page 5 */
public class SnExp implements TensorUnaryOperator {
  private final Tensor point;

  public SnExp(Tensor point) {
    this.point = VectorQ.require(point);
    if (point.length() < 2)
      throw TensorRuntimeException.of(point);
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor vector) {
    Scalar norm = Norm._2.ofVector(vector);
    Scalar sinc = Sinc.FUNCTION.apply(norm);
    return point.multiply(Cos.FUNCTION.apply(norm)).add(vector.multiply(sinc));
  }
}
