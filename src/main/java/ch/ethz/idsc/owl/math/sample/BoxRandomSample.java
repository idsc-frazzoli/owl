// code by jph
package ch.ethz.idsc.owl.math.sample;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

public class BoxRandomSample implements RandomSampleInterface {
  private final List<Distribution> distributions = new LinkedList<>();

  /** the parameters define the coordinate bounds of the axis-aligned box
   * from which the samples are drawn
   * 
   * @param min lower-left
   * @param max upper-right */
  public BoxRandomSample(Tensor min, Tensor max) {
    GlobalAssert.that(min.length() == max.length());
    for (int index = 0; index < min.length(); ++index)
      distributions.add(UniformDistribution.of(min.Get(index), max.Get(index)));
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample() {
    return Tensor.of(distributions.stream().map(RandomVariate::of));
  }
}
