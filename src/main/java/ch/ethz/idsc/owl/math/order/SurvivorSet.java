// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public class SurvivorSet {
  private Collection<Tensor> feasibleInputs;
  private int index;
  private Collection<UtilityFunction<Scalar, Scalar>> utilityFunctionVector;
  private Tensor slackVector;
  // private Scalar u_min = RealScalar.of(10000);

  public SurvivorSet(Collection<Tensor> feasibleInputs, int index, Collection<UtilityFunction<Scalar, Scalar>> utilityFunctionVector, Tensor slackVector) {
    this.feasibleInputs = feasibleInputs;
    this.index = index;
    this.utilityFunctionVector = utilityFunctionVector;
    this.slackVector = slackVector;
  }

  public final Collection<Tensor> getSurvivorSet() {
    // Stream to get u_min
    // Stream to get 
    
    Collection<Tensor> SurvivorSet = new LinkedList();
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
        SurvivorSet.add(x);
      } else if (isSmallerThanMin && withinSlack) {
        u_min = x.Get(index);
        // TODO CleanUP
        SurvivorSet.add(x);
      } else if (isSmallerThanMin && !withinSlack) {
        SurvivorSet.clear();
        SurvivorSet.add(x);
      }
    }
    return null;
  }
}
