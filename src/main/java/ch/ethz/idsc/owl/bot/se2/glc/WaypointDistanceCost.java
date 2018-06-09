// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.sca.Floor;

public class WaypointDistanceCost implements CostFunction, Serializable {
  private final int offPathCost = 1; // TODO magic const
  private final int max_y;
  private final Tensor scale;
  private final BufferedImage bufferedImage;
  private final Tensor image;
  private final List<Integer> resolution;

  /** @param waypoints
   * @param range vector of length 2 with entries in model space
   * @param pathWidth in pixels
   * @param resolution {width, height} */
  public WaypointDistanceCost(Tensor waypoints, Tensor range, float pathWidth, List<Integer> resolution) {
    this.resolution = resolution;
    max_y = resolution.get(0) - 1;
    // TODO the ordering of range and resolution at the moment is reversed:
    scale = Tensors.vector(resolution.get(1), resolution.get(0)).pmul(range.map(Scalar::reciprocal));
    float scaleX = scale.Get(1).number().floatValue();
    float scaleY = scale.Get(0).number().floatValue();
    // ---
    bufferedImage = new BufferedImage(resolution.get(0), resolution.get(1), BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(new Color(offPathCost, offPathCost, offPathCost));
    graphics.fillRect(0, 0, resolution.get(0), resolution.get(1));
    graphics.setColor(new Color(0, 0, 0));
    graphics.setStroke(new BasicStroke(pathWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    Tensor model2pixel = Tensors.matrix(new Number[][] { //
        { scaleX, 0, 0 }, { 0, -scaleY, max_y }, { 0, 0, 1 } });
    GeometricLayer geometricLayer = GeometricLayer.of(model2pixel);
    Path2D path = geometricLayer.toPath2D(waypoints);
    path.closePath();
    graphics.draw(path);
    image = ImageFormat.from(bufferedImage);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO;
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    // TODO using only the final state of the trajectory is not sampling independent
    return pointcost(trajectory.get(trajectory.size() - 1).state());
  }

  /* package */ Scalar pointcost(Tensor tensor) {
    if (tensor.length() != 2)
      tensor = tensor.extract(0, 2);
    Tensor pixel = Floor.of(tensor.pmul(scale)); // TODO floor is not required
    // code features redundancies for instance to ImageRegion
    int pix = pixel.Get(0).number().intValue();
    if (0 <= pix && pix < resolution.get(1)) {
      int piy = max_y - pixel.Get(1).number().intValue();
      if (0 <= piy && piy < resolution.get(0))
        return image.Get(piy, pix);
    }
    return RealScalar.of(offPathCost);
  }

  public Tensor image() {
    return image.unmodifiable();
  }

  /** @return */
  public BufferedImage visualization() {
    return bufferedImage;
  }
}
