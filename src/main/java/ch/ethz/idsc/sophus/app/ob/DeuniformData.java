// code by ob, jph
package ch.ethz.idsc.sophus.app.ob;

import java.util.stream.Stream;

import ch.ethz.idsc.subare.util.Coinflip;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum DeuniformData {
  ;
  /** @param stream
   * @param p_keep in interval [0, 1] probability of data to be kept
   * @return */
  public static <T> Stream<T> of(Stream<T> stream, Scalar p_keep) {
    Coinflip coinflip = Coinflip.of(p_keep);
    return stream.filter(i -> coinflip.tossHead()); //
  }

  /** @param tensor
   * @param p_keep in interval [0, 1] probability of data to be kept
   * @return */
  public static Tensor of(Tensor tensor, Scalar p_keep) {
    return Tensor.of(of(tensor.stream(), p_keep).map(Tensor::copy));
  }
}
