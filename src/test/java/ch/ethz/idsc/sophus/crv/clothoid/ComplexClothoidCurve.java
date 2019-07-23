// code by ureif
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Real;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** original implementation */
/* package */ enum ComplexClothoidCurve implements GeodesicInterface {
  INSTANCE;
  // ---
  private static final Tensor W = Tensors.vector(5, 8, 5).divide(RealScalar.of(18.0));
  private static final Tensor X = Tensors.vector(-1, 0, 1) //
      .multiply(Sqrt.FUNCTION.apply(RationalScalar.of(3, 5))) //
      .map(RealScalar.ONE::add) //
      .divide(RealScalar.of(2));
  private static final Scalar _1 = RealScalar.of(1.0);
  private static final Scalar _68 = RealScalar.of(68.0);
  private static final Scalar _46 = RealScalar.of(46.0);
  private static final Scalar _1_4 = RealScalar.of(0.25);

  @Override // from GeodesicInterface
  public ScalarTensorFunction curve(Tensor p, Tensor q) {
    Scalar p0 = ComplexScalar.of(p.Get(0), p.Get(1));
    Scalar a0 = p.Get(2);
    Scalar p1 = ComplexScalar.of(q.Get(0), q.Get(1));
    Scalar a1 = q.Get(2);
    // ---
    Scalar d = p1.subtract(p0);
    Scalar da = ArcTan2D.of(q.subtract(p));
    Scalar b0 = So2.MOD.apply(a0.subtract(da));
    Scalar b1 = So2.MOD.apply(a1.subtract(da));
    // ---
    Scalar f1 = b0.multiply(b0).add(b1.multiply(b1)).divide(_68);
    Scalar f2 = b0.multiply(b1).divide(_46);
    Scalar f3 = _1_4;
    Scalar bm = b0.add(b1).multiply(f1.subtract(f2).subtract(f3));
    ClothoidQuadraticEx clothoidQuadratic = new ClothoidQuadraticEx(b0, bm, b1);
    return t -> {
      Scalar _1_t = _1.subtract(t);
      Tensor wl = W.multiply(t);
      Tensor wr = W.multiply(_1_t);
      Tensor xl = X.multiply(t);
      Tensor xr = X.multiply(_1_t).map(t::add);
      Scalar il = wl.dot(xl.map(clothoidQuadratic)).Get();
      Scalar ir = wr.dot(xr.map(clothoidQuadratic)).Get();
      Scalar ret_p = p0.add(il.multiply(d).divide(il.add(ir)));
      Scalar ret_a = clothoidQuadratic.angle(t).add(da);
      return Tensors.of( //
          Real.FUNCTION.apply(ret_p), //
          Imag.FUNCTION.apply(ret_p), //
          ret_a);
    };
  }

  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar t) {
    return curve(p, q).apply(t);
  }
}
