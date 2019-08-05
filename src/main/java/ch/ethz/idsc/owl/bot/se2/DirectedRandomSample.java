// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

// TODO GJOEL not used yet. implementation is a bit unfinished: goal.get(2) is never used etc.
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
  private final List<Distribution> distributions = new ArrayList<>(3);
  private final Scalar mu;
  private Tensor goal;

  private DirectedRandomSample(Tensor min, Tensor max, Scalar mu, Tensor goal) {
    VectorQ.requireLength(min, 3);
    VectorQ.requireLength(max, 3);
    this.mu = mu;
    for (int index = 0; index < 3; ++index)
      distributions.add(index < 2 //
          ? UniformDistribution.of(min.Get(index), max.Get(index))
          : null); // FIXME GJOEL this will likely throw an exception later
    setGoal(goal);
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

  /** @param goal of the form {px, py, pa} */
  public void setGoal(Tensor goal) {
    this.goal = VectorQ.requireLength(goal, 3);
  }
}
