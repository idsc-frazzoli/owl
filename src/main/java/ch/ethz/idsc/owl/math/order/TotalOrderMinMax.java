// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class TotalOrderMinMax {
  // public static TotalOrderMinMax of(Tensor tensor) {
  // return new TotalOrderMinMax(tensor);
  // }
  public static Scalar TOmin(Tensor tensor) {
    Scalar min = tensor.Get(0);
    for (int i = 1; i < tensor.length(); i++) {
      min = (Scalars.lessEquals(min, tensor.Get(i))) ? min : tensor.Get(i);
    }
    return min;
  }

  public static Scalar TOmax(Tensor tensor) {
    Scalar max = tensor.Get(0);
    for (int i = 1; i < tensor.length(); i++) {
      max = (Scalars.lessEquals(tensor.Get(i), max)) ? max : tensor.Get(i);
    }
    return max;
  }

  public static void main(String[] args) {
    Tensor x1 = Tensors.vector(1, 2, 3, 4, 0.2);
    System.out.println(TotalOrderMinMax.TOmin(x1));
    System.out.println(TotalOrderMinMax.TOmax(x1));
  }
}