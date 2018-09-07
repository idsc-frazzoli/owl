package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

public class Pareto {
  public static boolean isDominated(Tensor first, Tensor second) {
    GlobalAssert.that(first.length() == second.length());
    return first.subtract(second).stream().allMatch(a -> Sign.isNegative(a.Get()));
  }
}
