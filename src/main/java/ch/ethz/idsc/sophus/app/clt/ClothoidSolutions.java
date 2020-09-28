// code by jph
package ch.ethz.idsc.sophus.app.clt;

import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import ch.ethz.idsc.sophus.clt.Clothoid;
import ch.ethz.idsc.sophus.clt.ClothoidBuilderImpl;
import ch.ethz.idsc.sophus.clt.ClothoidContext;
import ch.ethz.idsc.sophus.clt.ClothoidTangentDefect;
import ch.ethz.idsc.sophus.clt.mid.ClothoidQuadratic;
import ch.ethz.idsc.sophus.clt.par.ClothoidIntegration;
import ch.ethz.idsc.sophus.clt.par.ClothoidIntegrations;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Real;
import ch.ethz.idsc.tensor.sca.Sign;

/** function is s1 odd
 * function is s2 even */
public class ClothoidSolutions {
  /** -min == max for tests to pass */
  public static final Tensor LAMBDAS = Subdivide.of(-15.0, 15.0, 1001).unmodifiable();

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
  private final Tensor lambdas = Tensors.empty();

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
      }
    }
  }

  public Tensor lambdas() {
    return lambdas.copy();
  }

  public Stream<Clothoid> stream(ClothoidContext clothoidContext) {
    Builder<Clothoid> builder = Stream.builder();
    for (Tensor _lambda : lambdas) {
      ClothoidQuadratic clothoidQuadratic = CustomClothoidQuadratic.of(_lambda.Get());
      ClothoidIntegration clothoidIntegration = ClothoidIntegrations.ANALYTIC;
      ClothoidBuilderImpl clothoidBuilderImpl = new ClothoidBuilderImpl(clothoidQuadratic, clothoidIntegration);
      builder.accept(clothoidBuilderImpl.from(clothoidContext));
    }
    return builder.build();
  }
}
