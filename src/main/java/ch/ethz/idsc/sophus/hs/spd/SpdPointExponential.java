// code by jph
package ch.ethz.idsc.sophus.hs.spd;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.lie.MatrixExp;
import ch.ethz.idsc.tensor.lie.MatrixLog;
import ch.ethz.idsc.tensor.mat.Inverse;

/** SPD == Symmetric positive definite == Sym+
 * 
 * <pre>
 * Exp: sim (n) -> Sym+(n)
 * Log: Sym+(n) -> sim (n)
 * </pre>
 * 
 * Reference:
 * "Riemannian Geometric Statistics in Medical Image Analysis", 2020
 * Edited by Pennec, Sommer, Fletcher, p. 79
 * 
 * @see MatrixExp
 * @see MatrixLog */
public enum SpdPointExponential {
  ;
  // ---
  public static Tensor exp(Tensor p, Tensor w) {
    // TODO inefficient
    Tensor pp = SpdSqrt.of(p);
    Tensor pn = Inverse.of(pp);
    Tensor sym = pn.dot(w).dot(pn);
    sym = Transpose.of(sym).add(sym).multiply(RationalScalar.HALF);
    return pp.dot(SpdExponential.INSTANCE.exp(sym)).dot(pp);
  }

  public static Tensor log(Tensor p, Tensor q) {
    Tensor pp = SpdSqrt.of(p);
    Tensor pn = Inverse.of(pp);
    Tensor sym = pn.dot(q).dot(pn);
    sym = Transpose.of(sym).add(sym).multiply(RationalScalar.HALF);
    return pp.dot(SpdExponential.INSTANCE.log(sym)).dot(pp);
  }
}
