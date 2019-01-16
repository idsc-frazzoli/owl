// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;

public enum St1Exponential implements LieExponential {
  INSTANCE;
  // ---
  @Override // from LieExponential
  public Tensor exp(Tensor dlambdadt) {
    Scalar dlambda = dlambdadt.Get(0);
    Scalar dt = dlambdadt.Get(1);

    if (Scalars.isZero(dlambda)) {
      return Tensors.of(//
          RealScalar.ONE, //
          dt);
    }
    else {
      return Tensors.of(//
          Exp.FUNCTION.apply(dlambda), //
          (Exp.FUNCTION.apply(dlambda).subtract(RealScalar.ONE)).multiply(dt).divide(dlambda));
    }
  }

  @Override // from LieExponential
  public Tensor log(Tensor lambdat) {
    Scalar lambda = lambdat.Get(0);
    Scalar t = lambdat.Get(1);

    if (Scalars.isZero(lambda.subtract(RealScalar.ONE))) {
      return Tensors.of(//
          RealScalar.ZERO, //
          t);
    }
    else {
      return Tensors.of(//
          Log.FUNCTION.apply(lambda),//
          //TODO: Kontrollieren wieseo "abs" benötigt wird. sonst gibt es vorzeichenfehler. EVTL Fehler in formel?
          Log.FUNCTION.apply(lambda).multiply(t).divide(lambda.subtract(RealScalar.ONE)));
//      Log.FUNCTION.apply(lambda).multiply(t).divide(RealScalar.ONE.subtract(lambda)));
    }
  }
}

