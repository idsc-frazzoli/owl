// code by ob
package ch.ethz.idsc.sophus.sym;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class SymWeightsToSplits {
  public Scalar leftWeights = RealScalar.ZERO;
  public Scalar rightWeights = RealScalar.ZERO;
  public Tensor result = Tensors.empty();

  private static Scalar split(Scalar pL, Scalar pR) {
    return pR.divide(pL.add(pR));
  }

  // TODO OB: either work with symLinks => JH, or correct the calculation of weights
  Tensor recursion(Tensor tree, Tensor weights) {
    if (tree.get(0).length() == -1 && tree.get(1).length() == -1) {
      Scalar pL = weights.Get(Scalars.intValueExact(tree.Get(0)));
      Scalar pR = weights.Get(Scalars.intValueExact(tree.Get(1)));
      leftWeights = pL.add(pR);
      rightWeights = pL.add(pR);
      return Tensors.of(pL, pR, split(pL, pR));
    } else if (tree.get(0).length() == -1) {
      // make recursion to the right branch of the tree
      // calculate rightWeights
      leftWeights = weights.Get(Scalars.intValueExact(tree.Get(0)));
      return Tensors.of(tree.get(0), recursion(tree.get(1), weights), split(leftWeights, rightWeights));
    } else if (tree.get(1).length() == -1) {
      // make recursion to the left branch of the tree
      // calculate leftweights
      rightWeights = weights.Get(Scalars.intValueExact(tree.Get(1)));
      return Tensors.of(recursion(tree.get(0), weights), tree.get(1), split(leftWeights, rightWeights));
    } else {
      // take leftweights and rightweights
      return Tensors.of(recursion(tree.get(0), weights), recursion(tree.get(1), weights), split(leftWeights, rightWeights));
    }
  }

  public static void main(String[] args) {
    // Test inputs
    Tensor weights = Tensors.vector(0, 1, 2, 3, 4);
    Tensor tree = Tensors.of(Tensors.vector(0, 1), Tensors.of(Tensors.vector(2, 3), RealScalar.of(4)));
    // Test output:
    Tensor output = Tensors.of(Tensors.vector(0, 1, 1), Tensors.of(Tensors.vector(2, 3, 3.0 / 5.0), RealScalar.of(4), RationalScalar.of(4, 9)),
        RealScalar.of(0.9));
    SymWeightsToSplits symWeightsToSplits = new SymWeightsToSplits();
    System.out.println(symWeightsToSplits.recursion(tree, weights));
    System.err.println(output);
  }
}