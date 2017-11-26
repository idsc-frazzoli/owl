// code by jph
package ch.ethz.idsc.owl.math.sample;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;

public interface RandomSampleInterface extends Serializable {
  /** @return random sample from continuous or discrete set */
  Tensor randomSample();
}
