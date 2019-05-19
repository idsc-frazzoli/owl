// code by ob, jph
package ch.ethz.idsc.sophus.sym;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class SymWeightsToSplits {
  public final Tensor weights;
  public final Tensor tree;
  private Tensor result = Tensors.empty();

  public SymWeightsToSplits(Tensor tree, Tensor weights) {
    this.tree = tree;
    this.weights = processedWeights(tree, weights);
  }

  private Tensor treeContent(Tensor tree) {
    for (int index = 0; index < 2; ++index)
      if (tree.get(index).length() == -1)
        result.append(tree.get(index));
      else
        treeContent(tree.get(index));
    return result;
  }

  private Tensor processedWeights(Tensor tree, Tensor weights) {
    Tensor content = treeContent(tree);
    Tensor unique = Tensor.of(content.stream().distinct());
    // TODO OB the condition below causes SymWeightsToSplitsTest to fail:
    // TODO JPH: How is this condition useful?
    // if (content.length() != unique.length())
    // throw TensorRuntimeException.of(content, unique);
    Tensor multiplicities = Tensors.empty();
    for (int i = 0; i < unique.length(); ++i) {
      Scalar counter = RealScalar.ZERO;
      for (int j = 0; j < content.length(); ++j)
        if (unique.get(i).equals(content.get(j)))
          counter = counter.add(RealScalar.ONE);
      multiplicities.append(counter);
    }
    Tensor processedWeigths = Tensors.empty();
    for (int i = 0; i < weights.length(); ++i)
      processedWeigths.append(weights.get(i).divide(multiplicities.Get(i)));
    return processedWeigths;
  }

  private Tensor apply(Tensor tree) {
    final Scalar[] w = new Scalar[2];
    for (int index = 0; index < 2; ++index) {
      if (tree.get(index).length() == 2) { // incomplete computation
        tree.set(apply(tree.get(index)), index);
      }
      w[index] = ScalarQ.of(tree.get(index)) //
          ? weights.Get(tree.Get(index).number().intValue())
          : tree.Get(index, 3);
    }
    return tree.append(split(w[0], w[1])).append(w[0].add(w[1]));
  }

  public Tensor splits() {
    return apply(tree);
  }

  private static Scalar split(Scalar pL, Scalar pR) {
    return pR.divide(pL.add(pR));
  }
}