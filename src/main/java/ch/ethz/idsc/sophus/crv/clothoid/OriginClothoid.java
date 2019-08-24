// code by ureif
package ch.ethz.idsc.sophus.crv.clothoid;

import java.io.Serializable;

import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.sophus.sym.PolarScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Real;
import ch.ethz.idsc.tensor.sca.Sqrt;

public class OriginClothoid implements Serializable {
  private static final Scalar _1 = RealScalar.of(1.0);
  /** 3-point Gauss Legendre quadrature on interval [0, 1] */
  private static final Tensor W = Tensors.vector(5, 8, 5).divide(RealScalar.of(18.0));
  private static final Tensor X = Tensors.vector(-1, 0, 1) //
      .multiply(Sqrt.FUNCTION.apply(RationalScalar.of(3, 5))) //
      .map(RealScalar.ONE::add) //
      .divide(RealScalar.of(2));
  private static final Scalar X0 = X.Get(0);
  private static final Scalar X1 = X.Get(1);
  private static final Scalar X2 = X.Get(2);
  private static final Scalar W0 = W.Get(0);
  private static final Scalar W1 = W.Get(1);
  // ---
  private final Tensor qxy;
  private final Scalar qxy_arg;
  private final Scalar b0;
  private final Scalar b1;
  private final Scalar bm;

  /** @param q vector of the form {qx, qy, qa} */
  public OriginClothoid(Tensor q) {
    qxy = q.extract(0, 2);
    Scalar qarg = q.Get(2);
    // ---
    qxy_arg = ArcTan2D.of(qxy); // special case when diff == {0, 0}
    b0 = qxy_arg.negate(); // normal form T0 == b0
    b1 = qarg.subtract(qxy_arg); // normal form T1 == b1
    bm = ClothoidApproximation.f(b0, b1);
  }

  public final class Curve implements ScalarTensorFunction {
    private final ClothoidQuadratic clothoidQuadratic = new ClothoidQuadratic(b0, bm, b1);

    @Override
    public Tensor apply(Scalar t) {
      Scalar il = il(t);
      Scalar ir = ir(t);
      /** ratio z enforces interpolation of terminal points
       * t == 0 -> (0, 0)
       * t == 1 -> (1, 0) */
      Scalar z = il.divide(il.add(ir));
      return prod(z, qxy) //
          .append(qxy_arg.add(clothoidQuadratic.apply(t)));
    }

    /** @param t
     * @return approximate integration of exp i*clothoidQuadratic on [0, t] */
    private Scalar il(Scalar t) {
      Scalar v0 = exp_i(X0.multiply(t));
      Scalar v1 = exp_i(X1.multiply(t));
      Scalar v2 = exp_i(X2.multiply(t));
      return v0.add(v2).multiply(W0).add(v1.multiply(W1)).multiply(t);
    }

    /** @param t
     * @return approximate integration of exp i*clothoidQuadratic on [t, 1] */
    private Scalar ir(Scalar t) {
      Scalar _1_t = _1.subtract(t);
      Scalar v0 = exp_i(X0.multiply(_1_t).add(t));
      Scalar v1 = exp_i(X1.multiply(_1_t).add(t));
      Scalar v2 = exp_i(X2.multiply(_1_t).add(t));
      return v0.add(v2).multiply(W0).add(v1.multiply(W1)).multiply(_1_t);
    }

    /** @return approximate length */
    public Scalar length() {
      return Norm._2.ofVector(qxy).divide(one().abs());
    }

    /** @return approximate integration of exp i*clothoidQuadratic on [0, 1] */
    private Scalar one() {
      // longterm one could use gauss-legendre 5th on [0, 1]
      Scalar v0 = exp_i(X0);
      Scalar v1 = exp_i(X1);
      Scalar v2 = exp_i(X2);
      return v0.add(v2).multiply(W0).add(v1.multiply(W1));
    }

    private Scalar exp_i(Scalar t) {
      return PolarScalar.unit(clothoidQuadratic.apply(t));
    }
  }

  /** complex multiplication between z and vector[0]+i*vector[1]
   * 
   * @param z
   * @param vector of length 2 with entries that may be {@link Quantity}
   * @return vector of length 2 with real entries corresponding to real and imag of result */
  /* package */ static Tensor prod(Scalar z, Tensor vector) {
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
