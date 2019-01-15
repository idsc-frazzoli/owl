// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

/** curve on the 2-dimensional sphere
 * 
 * https://de.wikipedia.org/wiki/Loxodrome */
public class Loxodrome implements ScalarTensorFunction {
  /** @param angle */
  public static ScalarTensorFunction of(Scalar angle) {
    return new Loxodrome(angle);
  }

  // ---
  private final Scalar angle;

  private Loxodrome(Scalar angle) {
    this.angle = angle;
  }

  @Override
  public Tensor apply(Scalar scalar) {
    Scalar f = ArcTan.FUNCTION.apply(scalar.multiply(angle));
    Scalar cf = Cos.FUNCTION.apply(f);
    Scalar x = Cos.FUNCTION.apply(scalar).multiply(cf);
    Scalar y = Sin.FUNCTION.apply(scalar).multiply(cf);
    Scalar z = Sin.FUNCTION.apply(f);
    return Tensors.of(x, y, z);
  }
}
