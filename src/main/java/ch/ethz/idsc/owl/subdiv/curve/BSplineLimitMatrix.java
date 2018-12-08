// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.opt.BSplineFunction;

public enum BSplineLimitMatrix {
  ;
  /** @param degree of b-spline basis functions
   * @param n number of control points
   * @return */
  public static Tensor of(int degree, int n) {
    Tensor domain = Range.of(0, n);
    return Transpose.of(Tensor.of(IntStream.range(0, n) //
        .mapToObj(index -> domain.map(BSplineFunction.of(degree, UnitVector.of(n, index))))));
  }
}
