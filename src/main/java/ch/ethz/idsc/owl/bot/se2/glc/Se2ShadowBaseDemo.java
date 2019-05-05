// code by ynager, jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;

import ch.ethz.idsc.owl.bot.util.StreetScenario;
import ch.ethz.idsc.owl.bot.util.StreetScenarioData;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public abstract class Se2ShadowBaseDemo extends Se2CarDemo {
  static final StreetScenarioData STREET_SCENARIO_DATA = StreetScenario.S5.load();
  static final float PED_VELOCITY = 1.5f;
  static final float PED_RADIUS = 0.2f;
  static final Color PED_COLOR_LEGAL = new Color(211, 249, 114, 200);
  static final Tensor RANGE = Tensors.vector(57.2, 44.0);
  Tensor imagePed = STREET_SCENARIO_DATA.imagePedLegal;
  Tensor imageLid = STREET_SCENARIO_DATA.imagePedIllegal;
  ImageRegion imageRegionPed = new ImageRegion(imagePed, RANGE, false);
  ImageRegion imageRegionLid = new ImageRegion(imageLid, RANGE, true);
  TrajectoryRegionQuery trajectoryRegionQuery = SimpleTrajectoryRegionQuery.timeInvariant(imageRegionLid);
}
