// code by ob, jph
package ch.ethz.idsc.sophus.sym;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class SymWeightsToSplits {
  public final Tensor weights;

  public SymWeightsToSplits(Tensor weights) {
    // this.weights = weights.unmodifiable();
    this.weights = weights;
  }

  private Tensor processedWeights() {
    // Add function that checks if multiple elements of tree have the same value (e.g. 3 is used in two Averages [2,3] and [3,4])
    // and then multiply corresponding weights by the reciprocal of the appearance
    return Tensors.empty();
  }

  public Tensor recursion(Tensor tree) {
    final Scalar[] w = new Scalar[2];
    for (int index = 0; index < 2; ++index) {
      if (tree.get(index).length() == 2) // incomplete computation
        tree.set(recursion(tree.get(index)), index);
      w[index] = ScalarQ.of(tree.get(index))//
          ? weights.Get(tree.Get(index).number().intValue())
          : tree.Get(index, 3);
    }
    return tree.append(split(w[0], w[1])).append(w[0].add(w[1]));
  }

  private static Scalar split(Scalar pL, Scalar pR) {
    return pR.divide(pL.add(pR));
  }
}