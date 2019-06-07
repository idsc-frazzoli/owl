// code by ob
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Mean;

public enum SpectrogramAnalogous {
  INSTANCE;
  // TODO OB: replace with faster division
  private static Tensor elementwiseDivision(Tensor nominator, Tensor denominator) {
    Tensor result = Tensors.empty();
    for (int index = 0; index < nominator.length(); ++index) {
      Tensor temp = Tensors.empty();
      for (int j = 0; j < nominator.get(0).length(); ++j)
        temp.append(nominator.get(index).get(j).divide(denominator.get(index).Get(j)));
      result.append(temp);
    }
    return Mean.of(result);
  }

  /** TODO OB Make more generic for any type of filter
   * 
   * @param control
   * @param tensorUnaryOperator
   * @param index
   * @param fourierWindow
   * @return */
  public static Tensor of(Tensor control, TensorUnaryOperator tensorUnaryOperator, int index, TensorUnaryOperator fourierWindow) {
    Tensor diffRaw = Differences.of(control).get(Tensor.ALL, index);
    Tensor specRaw = fourierWindow.apply(diffRaw);
    // ---
    Tensor refined = tensorUnaryOperator.apply(control);
    Tensor diffRefined = Differences.of(refined).get(Tensor.ALL, index);
    // ---
    Tensor specRefined = fourierWindow.apply(diffRefined);
    // ---
    return elementwiseDivision(specRefined, specRaw);
  }
}