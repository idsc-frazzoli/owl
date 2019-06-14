// code by ureif
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Real;

/* package */ enum StaticHelper {
  ;
  /** complex multiplication
   * 
   * @param z
   * @param vector of length 2 with entries that may be {@link Quantity}
   * @return */
  public static Tensor prod(Scalar z, Tensor vector) {
    Scalar zr = Real.FUNCTION.apply(z);
    Scalar zi = Imag.FUNCTION.apply(z);
    Scalar v0 = vector.Get(0);
    Scalar v1 = vector.Get(1);
    return Tensors.of( //
        zr.multiply(v0).subtract(zi.multiply(v1)), //
        zi.multiply(v0).add(zr.multiply(v1)) //
    );
  }
}
