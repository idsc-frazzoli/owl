// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.hs.r2.R2RigidFamily;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

/** for images only rigid transformations are allowed */
public class R2xTImageStateTimeRegion implements Region<StateTime>, RenderInterface, Serializable {
  // ---
  private final ImageRegion imageRegion;
  private final R2RigidFamily rigidFamily;
  private final Supplier<Scalar> supplier;
  /** image render */
  private final RenderInterface renderInterface;

  /** @param imageRegion
   * @param rigidFamily
   * @param supplier */
  public R2xTImageStateTimeRegion(ImageRegion imageRegion, R2RigidFamily rigidFamily, Supplier<Scalar> supplier) {
    this.imageRegion = imageRegion;
    this.rigidFamily = rigidFamily;
    this.supplier = supplier;
    renderInterface = ImageRender.scale(RegionRenders.image(imageRegion.image()), imageRegion.scale());
  }

  @Override // from Region
  public boolean isMember(StateTime stateTime) {
    Tensor state = Extract2D.FUNCTION.apply(stateTime.state());
    Scalar time = stateTime.time();
    TensorUnaryOperator rev = rigidFamily.inverse(time);
    return imageRegion.isMember(rev.apply(state));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Scalar time = supplier.get();
    Tensor matrix = rigidFamily.forward_se2(time);
    geometricLayer.pushMatrix(matrix);
    renderInterface.render(geometricLayer, graphics);
    geometricLayer.popMatrix();
  }
}
