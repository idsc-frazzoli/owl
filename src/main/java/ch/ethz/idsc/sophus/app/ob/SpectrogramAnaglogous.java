// code by ob
package ch.ethz.idsc.sophus.app.ob;

import ch.ethz.idsc.sophus.filter.CenterFilter;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Mean;

public enum SpectrogramAnaglogous {
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

  // Make more generic for any type of filter
  public static Tensor transferFunction(Tensor control, int radius, TensorUnaryOperator tensorUnaryOperator, int signal, int windowDuration,
      int samplingFrequency) {
    Tensor refined = CenterFilter.of(tensorUnaryOperator, radius).apply(control);
    // ---
    Tensor diffRefined = Tensor.of(Differences.of(refined).stream().map(xya -> xya.Get(signal)));
    Tensor diffRaw = Tensor.of(Differences.of(control).stream().map(xya -> xya.Get(signal)));
    // ---
    FourierWindow fw = new FourierWindow(windowDuration, samplingFrequency);
    Tensor specRefined = fw.apply(diffRefined);
    Tensor specRaw = fw.apply(diffRaw);
    // ---
    Tensor quotient = elementwiseDivision(specRefined, specRaw);
    return quotient;
  }
}