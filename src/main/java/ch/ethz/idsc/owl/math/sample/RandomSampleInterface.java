// code by jph
package ch.ethz.idsc.owl.math.sample;

import ch.ethz.idsc.tensor.Tensor;

public interface RandomSampleInterface {
  /** @return random sample from continuous or discrete set */
  Tensor randomSample();
}
