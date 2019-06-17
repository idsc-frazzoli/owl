// code by ureif
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Real;

public enum ClothoidCurve implements GeodesicInterface {
  INSTANCE;
  // ---
  /** 2-point Gauss Legendre quadrature on interval [0, 1] */
  private static final Scalar HALF = RealScalar.of(0.5);
  private static final Tensor X = Tensors.vector(-1, 1) //
      .multiply(RealScalar.of(Math.sqrt(1 / 3))) //
      .map(RealScalar.ONE::add) //
      .divide(RealScalar.of(2));
  private static final Scalar _1 = RealScalar.of(1.0);
  private static final Scalar _68 = RealScalar.of(68.0);
  private static final Scalar _46 = RealScalar.of(46.0);
  private static final Scalar _1_4 = RealScalar.of(0.25);

  @Override // from GeodesicInterface
  public ScalarTensorFunction curve(Tensor p, Tensor q) {
    Tensor pxy = p.extract(0, 2);
    Scalar pa = p.Get(2);
    Tensor qxy = q.extract(0, 2);
    Scalar qa = q.Get(2);
    // ---
    Tensor diff = qxy.subtract(pxy);
    Scalar da = ArcTan2D.of(diff);
    Scalar b0 = So2.MOD.apply(pa.subtract(da));
    Scalar b1 = So2.MOD.apply(qa.subtract(da));
    // ---
    Scalar f1 = b0.multiply(b0).add(b1.multiply(b1)).divide(_68);
    Scalar f2 = b0.multiply(b1).divide(_46);
    Scalar f3 = _1_4;
    Scalar bm = b0.add(b1).multiply(f1.subtract(f2).subtract(f3));
    ClothoidQuadratic clothoidQuadratic = new ClothoidQuadratic(b0, bm, b1);
    return t -> {
      Tensor xl = X.multiply(t);
      Scalar il = Total.ofVector(xl.map(clothoidQuadratic)).multiply(HALF).multiply(t);
      // ---
      Scalar _1_t = _1.subtract(t);
      Tensor xr = X.multiply(_1_t).map(t::add);
      Scalar ir = Total.ofVector(xr.map(clothoidQuadratic)).multiply(HALF).multiply(_1_t);
      // ---
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
    };
  }

  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar t) {
    return curve(p, q).apply(t);
  }
}
