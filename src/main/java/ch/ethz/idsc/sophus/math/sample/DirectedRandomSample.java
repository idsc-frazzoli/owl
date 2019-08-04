// code by jph, gjoel
package ch.ethz.idsc.sophus.math.sample;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

// TODO GJOEL not used yet, not generic for sophus: move to rrts specific package in owl
public class DirectedRandomSample implements RandomSampleInterface, Serializable {
  /** the parameters define the coordinate bounds of the axis-aligned box
   * from which the samples are drawn
   *
   * @param min lower-left in SE2
   * @param max upper-right in SE2
   * @param mu of heading distribution
   * @param goal in SE2 */
  public static RandomSampleInterface of(Tensor min, Tensor max, Scalar mu, Tensor goal) {
    return new DirectedRandomSample(min, max, mu, goal);
  }

  // ---
  private final List<Distribution> distributions = new LinkedList<>();
  private final Scalar mu;
  private Tensor goal;

  private DirectedRandomSample(Tensor min, Tensor max, Scalar mu, Tensor goal) {
    setGoal(goal);
    this.mu = mu;
    for (int index = 0; index < 3; ++index) {
      if (index < 2) {
        Scalar minS = VectorQ.requireLength(min, 3).Get(index);
        Scalar maxS = VectorQ.requireLength(max, 3).Get(index);
        distributions.add(UniformDistribution.of(minS, maxS));
      } else
        distributions.add(null);
    }
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample(Random random) {
    Tensor sample = Tensors.empty();
    for (int i = 0; i < 3; i++) {
      if (i > 1) {
        Tensor direction = Extract2D.FUNCTION.apply(goal).subtract(sample);
        distributions.set(i, NormalDistribution.of(ArcTan2D.of(direction), mu));
      }
      sample.append(RandomVariate.of(distributions.get(i)));
    }
    return Tensor.of(distributions.stream().map(RandomVariate::of));
  }

  public void setGoal(Tensor goal) {
    this.goal = VectorQ.requireLength(goal, 3);
  }
}
