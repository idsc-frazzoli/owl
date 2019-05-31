// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.alg.Join;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Insert.html">Insert</a> */
public enum Insert {
  ;
  /** @param tensor
   * @param element
   * @param index
   * @return */
  public static Tensor of(Tensor tensor, Tensor element, int index) {
    return Join.of(tensor.extract(0, index), Tensors.of(element), tensor.extract(index, tensor.length()));
  }

  /** Wikipedia: In computer science, an in-place algorithm is an algorithm which transforms input
   * using no auxiliary data structure. However a small amount of extra storage space is allowed for
   * auxiliary variables. The input is usually overwritten by the output as the algorithm executes.
   * 
   * @param tensor
   * @param element
   * @param index
   * @throws Exception if tensor is unmodifiable
   * @throws Exception if index is not from the set {0, 1, ..., tensor.length()} */
  public static void inplace(Tensor tensor, Tensor element, int index) {
    Unprotect.list(tensor).add(index, element.copy());
  }
}
