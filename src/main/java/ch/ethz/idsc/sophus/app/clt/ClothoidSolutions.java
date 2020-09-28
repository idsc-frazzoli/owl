// code by jph
package ch.ethz.idsc.sophus.app.clt;

import java.io.Serializable;

import ch.ethz.idsc.sophus.clt.ClothoidTangentDefect;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Real;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sign;

public class ClothoidSolutions implements Serializable {
  private static final Chop CHOP = Chop._08;

  /** @param clip
   * @return */
  public static ClothoidSolutions of(Clip clip) {
    return of(clip, 101);
  }

  /** @param clip
   * @param n
   * @return */
  public static ClothoidSolutions of(Clip clip, int n) {
    return new ClothoidSolutions(Subdivide.increasing(clip, n));
  }

  /***************************************************/
  /** -min == max for tests to pass */
  final Tensor probes;

  public ClothoidSolutions(Tensor probes) {
    this.probes = probes.unmodifiable();
  }

  /** function is s1 odd
   * function is s2 even */
  public class Search implements Serializable {
    private final Tensor lambdas = Tensors.empty();
    public final Tensor defects_real;

    public Search(Scalar s1, Scalar s2) {
      ClothoidTangentDefect clothoidTangentDefect = ClothoidTangentDefect.of(s1, s2);
      ScalarUnaryOperator function = s -> Real.FUNCTION.apply(clothoidTangentDefect.apply(s));
      FindZero findZero = new FindZero(function, Sign::isPositive, CHOP);
      Tensor defects = probes.map(clothoidTangentDefect);
      defects_real = defects.map(Real.FUNCTION);
      // Tensor defects_imag = defects.map(Imag.FUNCTION);
      // ---
      boolean prev = Sign.isPositive(defects_real.Get(0));
      for (int index = 1; index < probes.length(); ++index) {
        boolean next = Sign.isPositive(defects_real.Get(index));
        if (prev && !next)
          lambdas.append(findZero.between( //
              probes.Get(index - 1), //
              probes.Get(index), //
              defects_real.Get(index - 1), //
              defects_real.Get(index)));
        prev = next;
      }
    }

    public Tensor lambdas() {
      return lambdas.unmodifiable();
    }
  }
}
