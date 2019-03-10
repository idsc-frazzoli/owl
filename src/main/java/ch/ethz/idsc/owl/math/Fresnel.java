// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Factorial;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** https://en.wikipedia.org/wiki/Fresnel_integral */
public class Fresnel implements ScalarUnaryOperator {
  private static final Clip CLIP = Clips.interval(-5, 5);
  // ---
  private static final ScalarUnaryOperator C = new Fresnel(1, 0);
  private static final ScalarUnaryOperator S = new Fresnel(3, 1);

  /** Careful: not consistent with Mathematica::FresnelC
   * input off by a factor, output off by a factor */
  public static ScalarUnaryOperator C() {
    return C;
  }

  /** Careful: not consistent with Mathematica::FresnelS
   * input off by a factor, output off by a factor */
  public static ScalarUnaryOperator S() {
    return S;
  }

  /** @return interval for evaluation of the fresnel function */
  public static Clip domain() {
    return CLIP;
  }

  // ---
  private final int first;
  private final int second;

  private Fresnel(int first, int second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public final Scalar apply(Scalar x) {
    domain().requireInside(x);
    if (ExactScalarQ.of(x))
      x = N.DOUBLE.apply(x);
    Scalar sum = x.zero();
    Scalar last;
    int n = 0;
    do {
      last = sum;
      Scalar f = RealScalar.of(4 * n + first);
      Scalar term = Power.of(x, f).divide(Factorial.of(2 * n + second)).divide(f);
      sum = sum.add(n % 2 == 0 ? term : term.negate());
      ++n;
    } while (!sum.equals(last));
    return sum;
  }
}
