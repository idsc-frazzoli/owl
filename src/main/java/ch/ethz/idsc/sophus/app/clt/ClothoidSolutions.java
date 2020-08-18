// code by jph
package ch.ethz.idsc.sophus.app.clt;

import java.util.Optional;

import ch.ethz.idsc.sophus.clt.ClothoidTangentDefect;
import ch.ethz.idsc.sophus.clt.LagrangeQuadratic;
import ch.ethz.idsc.sophus.clt.par.AnalyticClothoidIntegral;
import ch.ethz.idsc.sophus.clt.par.ClothoidIntegral;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Real;
import ch.ethz.idsc.tensor.sca.Sign;

/** function is s1 odd
 * function is s2 even */
public class ClothoidSolutions {
  /** -min == max for tests to pass */
  public static final Tensor LAMBDAS = Subdivide.of(-13.0, 13.0, 1001).unmodifiable();

  public static ClothoidSolutions of(Scalar s1, Scalar s2) {
    return new ClothoidSolutions(s1, s2);
  }

  public static ClothoidSolutions of(Number s1, Number s2) {
    return of(RealScalar.of(s1), RealScalar.of(s2));
  }

  /***************************************************/
  protected final ClothoidTangentDefect clothoidTangentDefect;
  private final Tensor defects;
  protected final Tensor defects_real;
  protected final Tensor defects_imag;
  private final Tensor lambdas = Tensors.empty();//
  private final Tensor lengths = Tensors.empty();

  public ClothoidSolutions(Scalar s1, Scalar s2) {
    clothoidTangentDefect = ClothoidTangentDefect.of(s1, s2);
    defects = LAMBDAS.map(clothoidTangentDefect);
    defects_real = defects.map(Real.FUNCTION);
    defects_imag = defects.map(Imag.FUNCTION);
    for (int index = 1; index < LAMBDAS.length(); ++index) {
      boolean prev = Sign.isPositive(defects_real.Get(index - 1));
      boolean next = Sign.isPositive(defects_real.Get(index));
      if (prev && !next) {
        Scalar lambda = RootDegree1.of( //
            LAMBDAS.Get(index - 1), //
            LAMBDAS.Get(index), //
            defects_real.Get(index - 1), //
            defects_real.Get(index));
        lambdas.append(lambda);
        Scalar b0 = s1.add(s2);
        Scalar b1 = s1.subtract(s2);
        LagrangeQuadratic lagrangeQuadratic = CustomClothoidQuadratic.of(lambda).lagrangeQuadratic(b0, b1);
        ClothoidIntegral clothoidIntegral = AnalyticClothoidIntegral.of(lagrangeQuadratic);
        Scalar length = Abs.of(clothoidIntegral.one()).reciprocal();
        lengths.append(length);
      }
    }
  }

  public Tensor lambdas() {
    return lambdas.copy();
  }

  protected Tensor lengths() {
    return lengths.copy();
  }

  public Optional<Scalar> shortest() {
    if (lengths.length() < 1)
      return Optional.empty();
    return Optional.of(lambdas.Get(ArgMin.of(lengths)));
  }

  public Tensor defects() {
    return lambdas.map(clothoidTangentDefect::defect);
  }

  public Optional<Scalar> getSolution(int index) {
    if (index < lambdas.length())
      return Optional.of(lambdas.Get(index));
    return Optional.empty();
  }
}
