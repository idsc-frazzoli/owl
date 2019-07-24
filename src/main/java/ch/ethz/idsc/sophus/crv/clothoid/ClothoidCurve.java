// code by ureif
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Real;

/** Reference:
 * Ulrich Reif slides */
/* package */ abstract class ClothoidCurve implements ScalarTensorFunction {
  protected static final Scalar _1 = RealScalar.of(1.0);
  // ---
  private final Tensor pxy;
  private final Tensor diff;
  private final Scalar da;
  protected final ClothoidQuadratic clothoidQuadratic;

  public ClothoidCurve(Tensor p, Tensor q) {
    pxy = p.extract(0, 2);
    Scalar pa = p.Get(2);
    Tensor qxy = q.extract(0, 2);
    Scalar qa = q.Get(2);
    // ---
    diff = qxy.subtract(pxy);
    da = ArcTan2D.of(diff); // special case when diff == {0, 0}
    Scalar b0 = So2.MOD.apply(pa.subtract(da)); // normal form T0 == b0
    Scalar b1 = So2.MOD.apply(qa.subtract(da)); // normal form T1 == b1
    // ---
    clothoidQuadratic = new ClothoidQuadratic(b0, ClothoidApproximation.f(b0, b1), b1);
  }

  /** @param t
   * @return approximate integration of exp i*clothoidQuadratic on [0, t] */
  protected abstract Scalar il(Scalar t);

  /** @param t
   * @return approximate integration of exp i*clothoidQuadratic on [t, 1] */
  protected abstract Scalar ir(Scalar t);

  @Override
  public final Tensor apply(Scalar t) {
    Scalar il = il(t);
    Scalar ir = ir(t);
    /** ratio z enforces interpolation of terminal points
     * t == 0 -> (0, 0)
     * t == 1 -> (1, 0) */
    Scalar z = il.divide(il.add(ir));
    return pxy.add(prod(z, diff)) //
        .append(clothoidQuadratic.apply(t).add(da));
  }

  /** complex multiplication
   * 
   * @param z
   * @param vector of length 2 with entries that may be {@link Quantity}
   * @return vector of length 2 with real entries corresponding to real and imag of result */
  static Tensor prod(Scalar z, Tensor vector) {
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
