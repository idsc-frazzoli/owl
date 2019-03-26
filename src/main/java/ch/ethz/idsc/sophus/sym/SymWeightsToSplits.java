// code by ob, jph
package ch.ethz.idsc.sophus.sym;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.red.VectorTotal;

public class SymWeightsToSplits {
  public final Tensor weights;

  public SymWeightsToSplits(Tensor weights) {
    this.weights = weights.unmodifiable();
  }

  Tensor recursion(Tensor tree) {
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

  public static void main(String[] args) {
    // Test inputs
    Tensor weights = Normalize.with(VectorTotal.FUNCTION).apply(Tensors.vector(2, 1, 2, 3, 4));
    System.out.println(weights);
    // // Test output:
    // Tensor output = Tensors.of(Tensors.vector(0, 1, 1), Tensors.of(Tensors.vector(2, 3, 3.0 / 5.0), RealScalar.of(4), RationalScalar.of(4, 9)),
    // RealScalar.of(0.9));
    SymWeightsToSplits symWeightsToSplits = new SymWeightsToSplits(weights);
    Tensor tree = Tensors.of(Tensors.vector(0, 1), Tensors.of(Tensors.vector(2, 3), RealScalar.of(4)));
    symWeightsToSplits.recursion(tree);
    System.out.println(tree);
    // System.err.println(output);
  }
}