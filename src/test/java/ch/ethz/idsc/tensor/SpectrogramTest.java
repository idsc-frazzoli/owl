// code by jph
package ch.ethz.idsc.tensor;

import java.util.Arrays;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class SpectrogramTest extends TestCase {
  public void testDefault() {
    Tensor tensor = Tensor.of(IntStream.range(0, 2000) //
        .mapToDouble(i -> Math.cos(i * 0.25 + (i / 20.0) * (i / 20.0))) //
        .mapToObj(RealScalar::of));
    assertEquals(Dimensions.of(Spectrogram.of(tensor)), Arrays.asList(32, 93, 4));
    assertEquals(Dimensions.of(Spectrogram.array(tensor)), Arrays.asList(32, 93));
  }
}
