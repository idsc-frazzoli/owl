// code by jl
package ch.ethz.idsc.owl.glc.adapter;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryGoalMarker;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.Tensor;

public abstract class TrajectoryGoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface, TrajectoryGoalMarker {
  private final List<Region<Tensor>> goalRegionList;

  public TrajectoryGoalManager(List<Region<Tensor>> goalRegionList) {
    super(new TimeInvariantRegion(RegionUnion.wrap(goalRegionList)));
    this.goalRegionList = goalRegionList;
  }

  public final List<Region<Tensor>> getGoalRegionList() {
    return goalRegionList;
  }

  public final List<Region<Tensor>> deleteRegionsBefore(Optional<StateTime> furthestState) {
    if (furthestState.isPresent()) {
      int deleteIndex = -1;
      int index = goalRegionList.size();
      while (index > 0) {
        index--;
        if (goalRegionList.get(index).isMember(furthestState.get().state())) {
          deleteIndex = index;
          break;
        }
      }
      final int deleteUntilIndex = deleteIndex;
      goalRegionList.removeIf(gr -> goalRegionList.indexOf(gr) < deleteUntilIndex);
    }
    return goalRegionList;
  }
}
