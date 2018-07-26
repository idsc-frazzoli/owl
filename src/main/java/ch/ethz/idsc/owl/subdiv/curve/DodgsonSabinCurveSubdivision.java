// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.planar.Cross2D;
import ch.ethz.idsc.owl.math.planar.SignedCurvature2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** 2005 Malcolm A. Sabin, Neil A. Dodgson:
 * A Circle-Preserving Variant of the Four-Point Subdivision Scheme
 * 
 * reproduces circles */
public enum DodgsonSabinCurveSubdivision implements CurveSubdivision {
  INSTANCE;
  // ---
  @Override
  public Tensor cyclic(Tensor tensor) {
    Tensor curve = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      curve.append(tensor.get(index));
      Tensor a = tensor.get((index - 1 + tensor.length()) % tensor.length());
      Tensor b = tensor.get((index + 0 + tensor.length()) % tensor.length());
      Tensor c = tensor.get((index + 1 + tensor.length()) % tensor.length());
      Tensor d = tensor.get((index + 2 + tensor.length()) % tensor.length());
      curve.append(midpoint(a, b, c, d));
    }
    return curve;
  }

  @Override
  public Tensor string(Tensor tensor) {
    throw new RuntimeException();
  }

  private static Tensor midpoint(Tensor A, Tensor B, Tensor C, Tensor D) {
    Scalar R = averageCurvature(A, B, C, D);
    Scalar lam = lambda(A, B, C, D, R);
    return intersectCircleLine(B, C, R, lam);
  }

  static Tensor intersectCircleLine(Tensor B, Tensor C, Scalar R, Scalar lam) {
    Tensor D = C.subtract(B);
    double d = Norm2Squared.ofVector(D).number().doubleValue();
    double l2 = lam.multiply(lam).number().doubleValue();
    double R2 = R.multiply(R).number().doubleValue();
    double fa = 1 / (1 + Math.sqrt(1 - R2 * d * 0.25));
    double fb = l2 / (1 + Math.sqrt(1 - l2 * R2 * d * 0.25));
    return Total.of(Tensors.of( //
        B.add(C).multiply(RealScalar.of(0.5)), //
        D.multiply(lam.divide(RealScalar.of(2))), //
        Cross2D.of(D).multiply(RealScalar.of(R.number().doubleValue() * Math.sqrt(d) * 0.25 * (fa - fb)))));
  }

  static Scalar lambda(Tensor A, Tensor B, Tensor C, Tensor D, Scalar R) {
    Scalar a = Norm._2.between(D, B);
    Scalar b = Norm._2.between(C, A);
    Scalar d = Norm2Squared.between(C, B);
    Scalar mu = b.divide(a);
    Scalar res = R.multiply(R).multiply(d).divide(RealScalar.of(4));
    Scalar H = res.divide(RealScalar.ONE.add(Sqrt.of(RealScalar.ONE.subtract(res))));
    Scalar mu1 = mu.add(RealScalar.ONE);
    Scalar mu1_2 = mu1.multiply(mu1);
    // (1 - H * 2 * mu / mu1_2)
    Scalar den = H.multiply(RealScalar.of(2)).multiply(mu).divide(mu1_2);
    return mu.subtract(RealScalar.ONE).divide(mu.add(RealScalar.ONE)).divide(RealScalar.ONE.subtract(den));
  }

  static Scalar averageCurvature(Tensor A, Tensor B, Tensor C, Tensor D) {
    Scalar a = Norm._2.between(D, B);
    Scalar b = Norm._2.between(C, A);
    return SignedCurvature2D.of(A, B, C).get().multiply(a).add( //
        SignedCurvature2D.of(B, C, D).get().multiply(b)) //
        .divide(a.add(b));
  }
}
