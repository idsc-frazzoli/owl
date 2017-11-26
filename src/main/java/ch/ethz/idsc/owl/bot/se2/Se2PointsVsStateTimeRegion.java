package ch.ethz.idsc.owl.bot.se2;

//// code by jph
// package ch.ethz.idsc.owly.demo.se2;
//
// import ch.ethz.idsc.owly.math.region.Region;
// import ch.ethz.idsc.owly.math.se2.Se2Bijection;
// import ch.ethz.idsc.owly.math.se2.Se2Family;
// import ch.ethz.idsc.owly.math.state.StateTime;
// import ch.ethz.idsc.tensor.Tensor;
//
/// ** used in se2 animation demo to check if footprint of vehicle intersects with obstacle region */
// public class Se2PointsVsStateTimeRegion implements Region<StateTime> {
// private final Tensor points;
// private final Region<Tensor> region;
//
// public Se2PointsVsStateTimeRegion(Tensor points, Region<Tensor> region) {
// this.points = points.copy().unmodifiable();
// this.region = region;
// }
//
// /** @param tensor of the form (x,y,theta)
// * @return true if any of the points subject to the given transformation are in region */
// @Override
// public boolean isMember(StateTime tensor) {
//// Se2Family se2Family = new Se2Family(t->);
//// Se2Bijection se2Bijection = new Se2Bijection(tensor);
//// return points.stream().map(se2Bijection.forward()).anyMatch(region::isMember);
// }
//
// public Tensor points() {
// return points;
// }
// }
