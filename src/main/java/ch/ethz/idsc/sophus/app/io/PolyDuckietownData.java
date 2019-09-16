// code by ob, jph
package ch.ethz.idsc.sophus.app.io;

import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;

public class PolyDuckietownData {
  public static PolyDuckietownData of(String string) {
    return new PolyDuckietownData(ResourceData.of(string));
  }

  // ---
  private final Tensor tensor;
  private final Tensor duckieNumber;

  public PolyDuckietownData(Tensor tensor) {
    this.tensor = Objects.requireNonNull(tensor);
    // extract list of all duckiebot numbers
    duckieNumber = Tensor.of(tensor.stream().map(row -> row.Get(6)).distinct());
  }

  /** @param tensor
   * @param duckiebot
   * @return */
  public Tensor filter(int duckiebot) {
    Tensor data = Tensors.empty();
    if (duckiebot < duckieNumber.length()) {
      Scalar id = duckieNumber.Get(duckiebot);
      for (int index = 0; index < tensor.length(); ++index)
        if (tensor.get(index).Get(6).equals(id))
          data.append(tensor.get(index));
      return Tensor.of(data.stream().map(row -> row.extract(2, 5)));
    }
    return Tensors.empty();
  }
}
