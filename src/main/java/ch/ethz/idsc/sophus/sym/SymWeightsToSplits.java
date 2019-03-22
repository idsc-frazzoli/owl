// code by jph
package ch.ethz.idsc.sophus.sym;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Total;

/* package */ class SymWeightsToSplits {
  static Tensor split(Tensor pL, Tensor pR) {
    return Total.of(pR).divide((Scalar) Total.of(pL).add(Total.of(pR)));
  }

  static Tensor conversion(Tensor weights) {
    // I'm not sure about the inputs I need
    // Either: Weight mask + tree shape
    // or: tree shape and kernel
    return Tensors.empty();
  }

  public static void main(String[] args) {
    // inputs
    Tensor weights = Tensors.vector(.2, .2, .2, .2, .2);
    Tensor tree = Tensors.of(Tensors.vector(0, 1), Tensors.of(Tensors.vector(2, 3), RealScalar.of(4)));
    // output:
    Tensor output = Tensors.of(Tensors.vector(0, 1, .5), Tensors.of(Tensors.vector(2, 3, .5), RealScalar.of(4), RationalScalar.of(1, 3)), RealScalar.of(0.6));
  }
}
