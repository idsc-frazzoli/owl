// code by jph
package ch.ethz.idsc.sophus.math;

import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class ScalarTensorMap {
  // public final
  public static void main(String[] args) {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    navigableMap.put(RealScalar.of(0.2), Tensors.vector(1, 2, 3));
    navigableMap.put(RealScalar.of(0.8), Tensors.vector(2, 3, 4));
    navigableMap.put(RealScalar.of(1.9), Tensors.vector(3, 4, 5));
    navigableMap.put(RealScalar.of(2.2), Tensors.vector(4, 5, 6));
    SortedMap<Scalar, Tensor> subMap = navigableMap.subMap(RealScalar.of(0.8), RealScalar.of(2));
    System.out.println(subMap);
    Tensor keys = Tensor.of(subMap.keySet().stream());
    System.out.println(keys);
    Tensor values = Tensor.of(subMap.values().stream());
    System.out.println(values);
  }
}
