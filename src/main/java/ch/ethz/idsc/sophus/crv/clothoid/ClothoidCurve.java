// code by ureif
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Real;

/* package */ abstract class ClothoidCurve implements ScalarTensorFunction {
  protected static final Scalar _1 = RealScalar.of(1.0);
  private static final Scalar _1_68 = RealScalar.of(1 / 68.0);
  private static final Scalar _1_46 = RealScalar.of(1 / 46.0);
  private static final Scalar _1_4 = RealScalar.of(0.25);
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
    da = ArcTan2D.of(diff);
    Scalar b0 = So2.MOD.apply(pa.subtract(da));
    Scalar b1 = So2.MOD.apply(qa.subtract(da));
    // ---
    Scalar f1 = b0.multiply(b0).add(b1.multiply(b1)).multiply(_1_68);
    Scalar f2 = b0.multiply(b1).multiply(_1_46);
    Scalar f3 = _1_4;
    Scalar bm = b0.add(b1).multiply(f1.subtract(f2).subtract(f3));
    clothoidQuadratic = new ClothoidQuadratic(b0, bm, b1);
  }

  /** @param t
   * @return integration of clothoidQuadratic on [0, t] */
  protected abstract Scalar il(Scalar t);

  /** @param t
   * @return integration of clothoidQuadratic on [t, 1] */
  protected abstract Scalar ir(Scalar t);

  @Override
  public final Tensor apply(Scalar t) {
    Scalar il = il(t);
    Scalar ir = ir(t);
    Tensor nc = StaticHelper.prod(il, diff).divide(il.add(ir));
    Tensor ret_p = pxy.add(nc);
    Scalar ret_a = clothoidQuadratic.angle(t).add(da);
    Scalar p0r = Real.FUNCTION.apply(ret_p.Get(0));
    Scalar p1r = Real.FUNCTION.apply(ret_p.Get(1));
    Scalar p0i = Imag.FUNCTION.apply(ret_p.Get(0));
    Scalar p1i = Imag.FUNCTION.apply(ret_p.Get(1));
    return Tensors.of( //
        p0r.subtract(p1i), //
        p0i.add(p1r), //
        ret_a);
  }
}
