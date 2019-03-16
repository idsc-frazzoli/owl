// code by astoll
package ch.ethz.idsc.owl.math.order;

/** Identity Utility Function which map real numbers onto itself.
 * @author Andre */
public class IdentityUtilityFunction {
  public static <Scalar> UtilityFunction<Scalar, Scalar> identity() {
    return new UtilityFunction<Scalar, Scalar>() {
      @Override // from Function
      public Scalar apply(Scalar x) {
        return x;
      }
    };
  }
}
