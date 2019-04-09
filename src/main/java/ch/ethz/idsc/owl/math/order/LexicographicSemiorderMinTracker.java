// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Ordering;

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

  public void updateFeasibleInputs() {
    // create tensor of VectorScalars
    Tensor vector = Tensors.empty();
    feasibleInputs.stream().forEach(x -> vector.append(VectorScalar.of(x)));
    // create array of lexicographically sorted indices
    int[] indices = Ordering.INCREASING.of(vector);
    System.out.println(vector);
    List<Integer> indicesList = Arrays.stream(indices).boxed().collect(Collectors.toList());
    System.out.println(indicesList);
    // eliminate elements where current value is higher than threshold (e.g. u_min + slack at index)
    for (int index = 0; index < slackVector.length(); ++index) {
      Iterator<Integer> iterator = indicesList.iterator();
      Scalar slack = slackVector.Get(index);
      // initialize u_min
      Scalar u_min = ((VectorScalar) vector.get(iterator.next())).at(index);
      while (iterator.hasNext()) {
        // get current value
        Scalar current_u = ((VectorScalar) vector.get(iterator.next())).at(index);
        // if current is lower then u_min it becomes the new u_min
        u_min = Scalars.lessEquals(u_min, current_u) ? u_min : current_u;
        // remove index if current value is bigger then threshold
        if (!filterCriterion(current_u, u_min.add(slack))) {
          iterator.remove();
        }
      }
    }
    // feasibleInputs.clear();
    // for (int i : indicesList) {
    // feasibleInputs.add(vector.get(i));
    // }
  }

  @Override
  public void digest(Tensor x) {
    if (x.length() != slackVector.length())
      throw new RuntimeException("Tensor x has wrong dimension");
    if (feasibleInputs.isEmpty()) {
      feasibleInputs.add(x);
      // System.out.println(x);
      return;
    }
    feasibleInputs.add(x);
    updateFeasibleInputs();
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
