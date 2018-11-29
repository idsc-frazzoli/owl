// code by jph
package ch.ethz.idsc.owl.math.sample;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.RandomVariate;

/** RandomSampleInterface is a generalization of {@link RandomVariate}.
 * 
 * RandomSampleInterface produces tensors from a multi-variate probability distribution.
 * 
 * Examples: {@link BoxRandomSample}, {@link CircleRandomSample} */
public interface RandomSampleInterface {
  /** @return random sample from continuous or discrete set */
  Tensor randomSample();
}
