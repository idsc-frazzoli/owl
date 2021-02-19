// code by gjoel
package ch.ethz.idsc.owl.lane;

import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.RotateLeft;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.red.ArgMin;

public class LaneSegment implements LaneInterface {
  /** @param laneInterface
   * @param start
   * @param end
   * @return */
  public static LaneInterface of(LaneInterface laneInterface, Tensor start, Tensor end) {
    int fromIdx = ArgMin.of(Tensor.of(laneInterface.midLane().stream().map(start::subtract).map(Extract2D.FUNCTION).map(Vector2Norm::of)));
    int toIdx = ArgMin.of(Tensor.of(laneInterface.midLane().stream().map(end::subtract).map(Extract2D.FUNCTION).map(Vector2Norm::of)));
    return new LaneSegment(laneInterface, fromIdx, toIdx);
  }

  private final LaneInterface laneInterface;
  private final int fromIdx;
  private final int toIdx;

  private LaneSegment(LaneInterface laneInterface, int fromIdx, int toIdx) {
    this.laneInterface = laneInterface;
    this.fromIdx = fromIdx;
    this.toIdx = toIdx;
  }

  @Override // from LaneInterface
  public Tensor controlPoints() {
    int fromIdx = ArgMin.of(Tensor.of(laneInterface.controlPoints().stream() //
        .map(laneInterface.midLane().get(this.fromIdx)::subtract).map(Extract2D.FUNCTION).map(Vector2Norm::of)));
    int toIdx = ArgMin.of(Tensor.of(laneInterface.controlPoints().stream() //
        .map(laneInterface.midLane().get(this.toIdx)::subtract).map(Extract2D.FUNCTION).map(Vector2Norm::of)));
    int idx = Math.floorMod(toIdx - fromIdx, laneInterface.controlPoints().length());
    return RotateLeft.of(laneInterface.controlPoints(), fromIdx).extract(0, idx + 1);
  }

  @Override // from LaneInterface
  public Tensor midLane() {
    return segment(laneInterface.midLane());
  }

  @Override // from LaneInterface
  public Tensor leftBoundary() {
    return segment(laneInterface.leftBoundary());
  }

  @Override // from LaneInterface
  public Tensor rightBoundary() {
    return segment(laneInterface.rightBoundary());
  }

  @Override // from LaneInterface
  public Tensor margins() {
    return segment(laneInterface.margins());
  }

  private Tensor segment(Tensor tensor) {
    int idx = Math.floorMod(toIdx - fromIdx, tensor.length());
    return RotateLeft.of(tensor, fromIdx).extract(0, idx + 1);
  }
}
