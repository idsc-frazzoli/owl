// code by jph
package ch.ethz.idsc.sophus.space;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sinc;

/** implementation is based on the function "strans" taken from
 * "Freeform Curves on Spheres of Arbitrary Dimension"
 * by Scott Schaefer and Ron Goldman, page 5 */
public enum SnTranslate {
  ;
  public static Tensor translate(Tensor point, Tensor vector) {
    Scalar v = Norm._2.ofVector(vector);
    Scalar sinc = Sinc.FUNCTION.apply(v);
    return point.multiply(Cos.FUNCTION.apply(v)).add(vector.multiply(sinc));
  }
}
