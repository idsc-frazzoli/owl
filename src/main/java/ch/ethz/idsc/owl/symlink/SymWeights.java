// code by jph
package ch.ethz.idsc.owl.symlink;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

class SymWeights {
  public static Tensor of(SymScalar root) {
    return new SymWeights(root).vector();
  }

  // ---
  private final Tensor sum = Array.zeros(20);
  private int max = 0;

  private SymWeights(SymScalar root) {
    visit(RealScalar.ONE, root);
  }

  private void visit(Scalar weight, SymScalar root) {
    if (root.isScalar()) {
      Scalar scalar = root.tensor().Get();
      int index = scalar.number().intValue();
      max = Math.max(max, index);
      sum.set(v -> v.add(weight), index);
    } else {
      visit(weight.multiply(RealScalar.ONE.subtract(root.ratio())), root.getP());
      visit(weight.multiply(root.ratio()), root.getQ());
    }
  }

  Tensor vector() {
    return sum.extract(0, max + 1);
  }
}
