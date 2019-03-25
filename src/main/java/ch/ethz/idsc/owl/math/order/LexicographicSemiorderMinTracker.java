// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/** Creates minTracker for a lexicographic semiorder.
 * The minimal elements for a lexicographic semiorder is the iteratively constructed set
 * where all elements are discarded which are not minimal w.r.t the first semiorder. Then from this remaining
 * set all elements are discarded which are not minimal with respect to the second semiorder and so on. */
public class LexicographicSemiorderMinTracker implements MinTracker<Tensor> {
  public static LexicographicSemiorderMinTracker withList(Tensor slackVector) {
    return new LexicographicSemiorderMinTracker(slackVector, new LinkedList<>());
  }

  public static LexicographicSemiorderMinTracker withSet(Tensor slackVector) {
    return new LexicographicSemiorderMinTracker(slackVector, new HashSet<>());
  }

  private final Collection<Tensor> feasibleInputs;
  private final Tensor slackVector;

  private LexicographicSemiorderMinTracker(Tensor slackVector, Collection<Tensor> feasibleInputs) {
    this.feasibleInputs = feasibleInputs;
    this.slackVector = slackVector;
  }

  /** Filters all elements which are within the slack of the "absolute" minimum.
   * 
   * @param x_i: Coordinate of element x
   * @param threshold = u_min + slack
   * @return true or false */
  public static boolean filterCriterion(Scalar x_i, Scalar threshold) {
    return Scalars.lessEquals(x_i, threshold);
  }

  public Collection<Tensor> getFeasibleInputs() {
    return feasibleInputs;
  }

  @Override
  public void digest(Tensor x) {
    if (x.length() != slackVector.length())
      throw new RuntimeException("Tensor x has wrong dimension");
    if (!feasibleInputs.contains(x))
      feasibleInputs.add(x);
  }

  @Override
  public Collection<Tensor> getMinElements() {
    Collection<Tensor> minElements = feasibleInputs;
    for (int i = 0; i < slackVector.length(); ++i) {
      if (minElements.size() == 1)
        return minElements;
      int index = i;
      Scalar u_min = minElements.stream().map(x -> x.Get(index)).min(Scalars::compare).get();
      Scalar slack = slackVector.Get(index);
      minElements = minElements.stream() //
          .filter(x -> filterCriterion(x.Get(index), u_min.add(slack))) //
          .collect(Collectors.toList());
    }
    return minElements;
  }
}
