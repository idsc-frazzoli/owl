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
import ch.ethz.idsc.owl.math.region.FlipYXTensorInterp;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ImageFormat;

public class WaypointDistanceCost implements CostFunction, Serializable {
  private static final int OFF_PATH_COST = 1;
  // ---
  private final int max_y;
  private final Tensor scale;
  private final BufferedImage bufferedImage;
  private final Tensor image;
  final FlipYXTensorInterp<Scalar> flipYXTensorInterp;

  /** @param waypoints
   * @param range vector of length 2 with entries in model space
   * @param pathWidth in pixels
   * @param resolution {width, height} */
  public WaypointDistanceCost(Tensor waypoints, Tensor range, float pathWidth, List<Integer> resolution) {
    max_y = resolution.get(0) - 1;
    scale = Tensors.vector(resolution.get(1), resolution.get(0)).pmul(range.map(Scalar::reciprocal));
    float scaleX = scale.Get(1).number().floatValue();
    float scaleY = scale.Get(0).number().floatValue();
    // ---
    bufferedImage = new BufferedImage(resolution.get(0), resolution.get(1), BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(new Color(OFF_PATH_COST, OFF_PATH_COST, OFF_PATH_COST));
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
    flipYXTensorInterp = new FlipYXTensorInterp<>(image, range, value -> value, RealScalar.of(OFF_PATH_COST));
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO;
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return flipYXTensorInterp.at(glcNode.state());
  }

  public Tensor image() {
    return image.unmodifiable();
  }

  /** @return */
  public BufferedImage visualization() {
    return bufferedImage;
  }
}
