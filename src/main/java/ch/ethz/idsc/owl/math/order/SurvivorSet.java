// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public class SurvivorSet {
  private final Collection<Tensor> feasibleInputs;
  // private int index;
  private final int dim;
  private final Collection<UtilityFunction<Scalar, Scalar>> utilityFunctionVector;
  private final Tensor slackVector;

  public SurvivorSet(Collection<Tensor> feasibleInputs, Collection<UtilityFunction<Scalar, Scalar>> utilityFunctionVector, Tensor slackVector) {
    this.feasibleInputs = feasibleInputs;
    this.utilityFunctionVector = utilityFunctionVector;
    this.slackVector = slackVector;
    this.dim = dimChecker();
  }

  public final Collection<Tensor> getSurvivorSetStream(Collection<Tensor> survivorSet, int index) {
    Scalar u_min = feasibleInputs.stream().map(x -> x.Get(index)).min(Scalars::compare).get();
    Scalar slack = slackVector.Get(index);
    // TODO ASTOLL/JPH recursion not plausible
    Collection<Tensor> z = survivorSet.stream() //
        .filter(x -> !discardTest(x.Get(index), u_min.add(slack))) //
        .collect(Collectors.toList());
    if (index < dim - 1) {
      int new_index = index + 1;
      getSurvivorSetStream(z, new_index);
    }
    return z;
  }

  public final int dimChecker() {
    if (feasibleInputs.isEmpty())
      throw new RuntimeException("Empty Set");
    long dimSet = feasibleInputs.stream().mapToInt(Tensor::length).distinct().limit(2).count();
    if (dimSet != 1)
      throw new RuntimeException("Elements to compare not of same size");
    return 3; // TODO ASTOLL what does this 3 mean?!
  }

  public static boolean discardTest(Scalar x, Scalar u_min) {
    return Scalars.lessEquals(u_min, x);
  }

  public final Collection<Tensor> getSurvivorSet(int index) {
    Collection<Tensor> survivorSet = new LinkedList<>();
    Iterator<Tensor> iterator = feasibleInputs.iterator();
    Tensor first = iterator.next();
    Scalar u_min = first.Get(index);
    while (iterator.hasNext()) {
      Tensor x = iterator.next();
      // ---
      boolean withinSlack = Scalars.lessEquals((u_min.subtract(x.Get(index))).abs(), slackVector.Get(index));
      boolean isSmallerThanMin = Scalars.lessThan(u_min, x.Get(index));
      boolean isLargerThanMin = Scalars.lessThan(x.Get(index), u_min);
      // ---
      if (isLargerThanMin && !withinSlack)
        continue;
      else //
      if (isLargerThanMin && withinSlack)
        survivorSet.add(x);
      else //
      if (isSmallerThanMin && withinSlack) {
        u_min = x.Get(index);
        cleanUp(survivorSet, u_min, index);
        survivorSet.add(x);
      } else //
      if (isSmallerThanMin && !withinSlack) {
        survivorSet.clear();
        survivorSet.add(x);
      }
    }
    if (index < dim)
      return getSurvivorSet(++index);
    return survivorSet;
  }

  private static void cleanUp(Collection<Tensor> survivorSet, Scalar u_min, int index) {
    Iterator<Tensor> iterator = survivorSet.iterator();
    while (iterator.hasNext()) {
      Tensor x = iterator.next();
      // TODO change threshold to u_min plus slack
      if (Scalars.lessThan(u_min, x.Get(index))) {
        iterator.remove();
      }
    }
  }
}
