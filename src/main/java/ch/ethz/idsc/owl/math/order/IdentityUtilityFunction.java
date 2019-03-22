// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Scalar;

/** Identity Utility Function which map real numbers onto itself.
 * @author Andre */
public enum IdentityUtilityFunction {
  ;
  public static UtilityFunction<Scalar, Scalar> identity() {
    return x -> x;
  }
}
