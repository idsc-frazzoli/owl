// code by jph
package ch.ethz.idsc.tensor.lie;

import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.TensorRank;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.sca.Factorial;

// TODO JPH TENSOR 081 obsolete
public enum Symmetrize {
  ;
  /** @param tensor of any rank with dimensions [n, n, ..., n]
   * @return symmetric tensor, i.e. invariant under transpose
   * @throws Exception if given tensor does not have regular dimensions */
  public static Tensor of(Tensor tensor) {
    int rank = TensorRank.of(tensor);
    return Permutations.of(Range.of(0, rank)).stream() //
        .map(permutation -> Transpose.of(tensor, IntStream.range(0, rank) //
            .mapToObj(index -> permutation.Get(index).number()) //
            .toArray(Integer[]::new)))
        .reduce(Tensor::add).get() //
        .divide(Factorial.of(rank));
  }
}
