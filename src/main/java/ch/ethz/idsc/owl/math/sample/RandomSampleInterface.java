// code by jph
package ch.ethz.idsc.owl.math.sample;

import java.util.Random;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.RandomVariateInterface;

/** RandomSampleInterface is a generalization of {@link RandomVariateInterface}.
 * 
 * RandomSampleInterface produces tensors from a multi-variate probability distribution.
 * 
 * Examples: {@link BoxRandomSample}, {@link CircleRandomSample} */
public interface RandomSampleInterface {
  /** @return random sample from continuous or discrete set */
  Tensor randomSample(Random random);
}
