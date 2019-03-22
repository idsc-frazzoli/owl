// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public class SurvivorSet {
  private Collection<Tensor> feasibleInputs;
  // private int index;
  private int dim;
  private Collection<UtilityFunction<Scalar, Scalar>> utilityFunctionVector;
  private Tensor slackVector;

  public SurvivorSet(Collection<Tensor> feasibleInputs, Collection<UtilityFunction<Scalar, Scalar>> utilityFunctionVector, Tensor slackVector) {
    this.feasibleInputs = feasibleInputs;
    this.utilityFunctionVector = utilityFunctionVector;
    this.slackVector = slackVector;
    this.dim = dimChecker();
  }

  public final Collection<Tensor> getSurvivorSetStream(Collection<Tensor> survivorSet, int index) {
    Scalar u_min = feasibleInputs.stream().map((x -> x.Get(index))).min(Scalars::compare).get();
    Scalar slack = slackVector.Get(index);
    Collection<Tensor> Z = survivorSet.stream().filter(x -> !discardTest(x.Get(index), u_min.add(slack))).collect(Collectors.toList());
    if (index < dim - 1) {
      int new_index = index + 1;
      getSurvivorSetStream(Z, new_index);
    }
    return Z;
  }

  public final int dimChecker() {
    if (feasibleInputs.isEmpty())
      throw new RuntimeException("Empty Set");
    Set<Integer> dimSet = feasibleInputs.stream().map(x -> x.length()).collect(Collectors.toSet());
    if (dimSet.size() != 1) {
      throw new RuntimeException("Elements to compare not of same size");
    }
    return 3;
  }

  public final boolean discardTest(Scalar x, Scalar u_min) {
    return Scalars.lessEquals(u_min, x);
  }

  public final Collection<Tensor> getSurvivorSet(int index) {
    Collection<Tensor> survivorSet = new LinkedList();
    Iterator<Tensor> iterator = feasibleInputs.iterator();
    Tensor first = iterator.next();
    Scalar u_min = first.Get(index);
    while (iterator.hasNext()) {
      Tensor x = iterator.next();
      // --
      boolean withinSlack = Scalars.lessEquals((u_min.subtract(x.Get(index))).abs(), slackVector.Get(index));
      boolean isSmallerThanMin = Scalars.lessThan(u_min, x.Get(index));
      boolean isLargerThanMin = Scalars.lessThan(x.Get(index), u_min);
      // __
      if (isLargerThanMin && !withinSlack) {
        continue;
      } else if (isLargerThanMin && withinSlack) {
        survivorSet.add(x);
      } else if (isSmallerThanMin && withinSlack) {
        u_min = x.Get(index);
        CleanUp(survivorSet, u_min, index);
        survivorSet.add(x);
      } else if (isSmallerThanMin && !withinSlack) {
        survivorSet.clear();
        survivorSet.add(x);
      }
    }
    if (index < dim) {
      return getSurvivorSet(++index);
    }
    return survivorSet;
  }

  private final void CleanUp(Collection<Tensor> survivorSet, Scalar u_min, int index) {
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
