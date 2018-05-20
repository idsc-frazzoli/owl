// code by jph
package ch.ethz.idsc.owl.math.sample;

import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;

public enum UniformRandomSample {
  ;
  public static RandomSampleInterface of(Distribution distribution, int length) {
    return () -> RandomVariate.of(distribution, length);
  }
}
