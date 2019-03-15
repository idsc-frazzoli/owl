// code by astoll
package ch.ethz.idsc.owl.math.order;

public enum IdentityUtilityFunction {
  ;
  public static <Scalar> UtilityFunction<Scalar, Scalar> identity() {
    return new UtilityFunction<Scalar, Scalar>() {
      @Override // from Function
      public Scalar apply(Scalar x) {
        return x;
      }
    };
  }
}
