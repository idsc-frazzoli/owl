// code by jph and jl
package ch.ethz.idsc.owl.glc.adapter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.data.tree.ExpandInterface;
import ch.ethz.idsc.owl.data.tree.ObservingExpandInterface;
import ch.ethz.idsc.owl.data.tree.StateCostNode;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;

public class Expand<T extends StateCostNode> {
  protected final ExpandInterface<T> expandInterface;
  protected Supplier<Boolean> isContinued = () -> true;
  protected int expandCount = 0;

  public Expand(ExpandInterface<T> expandInterface) {
    this.expandInterface = Objects.requireNonNull(expandInterface);
  }

  /** @return number of expand operations */
  public int getExpandCount() {
    return expandCount;
  }

  public void setContinued(Supplier<Boolean> isContinued) {
    this.isContinued = isContinued;
  }

  /** fixed number of invocations of expand(...)
   * however, earlier abort may be possible for instance to due lack of nodes to expand from
   *
   * @param limit */
  public void steps(int limit) {
    expand(limit, () -> false);
  }

  /** iterates until expansion creates a first node goal region
   *
   * @param limit */
  public void findAny(int limit) {
    expand(limit, () -> expandInterface.getBest().isPresent());
  }

  /** iterates until expansion creates a first node goal region or timeLimit is reached
   *
   * @param timeLimit */
  public void maxTime(Scalar timeLimit) {
    final Timing timing = Timing.started();
    final double time = timeLimit.number().doubleValue();
    expand(Integer.MAX_VALUE, () -> {
      double t = timing.seconds();
      boolean bool = t >= time;
      if (bool && !expandInterface.getBest().isPresent())
        System.out.println("*** TimeLimit reached -- No Goal was found ***");
      return bool;
    });
  }

  private void expand(int limit, Supplier<Boolean> isFinished) {
    if (expandInterface instanceof ObservingExpandInterface && //
        ((ObservingExpandInterface) expandInterface).isObserving()) {
      final Map<Double, Scalar> observations = new LinkedHashMap<>();
      Timing timing = Timing.started();
      Supplier<Boolean> isFinished_ = () -> {
        expandInterface.getBest().ifPresent(node -> observations.put(timing.seconds(), node.costFromRoot()));
        return isFinished.get();
      };
      expansionLoop(limit, isFinished_);
      ((ObservingExpandInterface) expandInterface).process(observations);
    } else
      expansionLoop(limit, isFinished);
  }

  private void expansionLoop(int limit, Supplier<Boolean> isFinished) {
    while (0 <= --limit && !isFinished.get() && isContinued.get()) {
      Optional<T> next = expandInterface.pollNext();
      if (next.isPresent()) {
        expandInterface.expand(next.get());
        ++expandCount;
      } else { // queue is empty
        System.out.println("*** Queue is empty -- No Goal was found ***");
        break;
      }
    }
  }
}
