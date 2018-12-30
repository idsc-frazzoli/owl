// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Factorial;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ abstract class FresnelBase implements ScalarUnaryOperator {
  private static final Clip CLIP = Clip.function(0, 5);

  @Override
  public final Scalar apply(Scalar x) {
    CLIP.requireInside(x.abs());
    if (ExactScalarQ.of(x))
      x = N.DOUBLE.apply(x);
    Scalar sum = x.zero();
    Scalar last;
    int n = 0;
    do {
      last = sum;
      Scalar f = RealScalar.of(4 * n + first());
      Scalar term = Power.of(x, f).divide(Factorial.of(2 * n + second())).divide(f);
      sum = sum.add(n % 2 == 0 ? term : term.negate());
      ++n;
    } while (!sum.equals(last));
    return sum;
  }

  abstract int first();

  abstract int second();
}
