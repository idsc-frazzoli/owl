// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.sca.Floor;

public class WaypointDistanceCost implements CostFunction, Serializable {
  private static final List<Integer> DIMENSIONS = Arrays.asList(640, 640); // TODO magic const

  public static CostFunction of(Tensor waypoints, Tensor range, float pathWidth) {
    return new WaypointDistanceCost(waypoints, range, pathWidth);
  }
  // ---

  private final Scalar outside = RealScalar.ONE;
  private final Tensor scale;
  private final int max_y;
  public final Tensor image;

  private WaypointDistanceCost(Tensor waypoints, Tensor range, float pathWidth) {
    max_y = DIMENSIONS.get(0) - 1;
    scale = Tensors.vector(DIMENSIONS.get(1), DIMENSIONS.get(0)).pmul(range.map(Scalar::reciprocal));
    float scaleX = scale.Get(1).number().floatValue();
    float scaleY = scale.Get(0).number().floatValue();
    // ---
    BufferedImage buffImage = new BufferedImage(600, 600, BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D graphics = (Graphics2D) buffImage.getGraphics();
    graphics.setColor(new Color(1, 1, 1));
    graphics.fillRect(0, 0, DIMENSIONS.get(0), DIMENSIONS.get(1));
    graphics.setColor(new Color(0, 0, 0));
    graphics.setStroke(new BasicStroke(pathWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    // ---
    float x0 = waypoints.get(waypoints.length() - 1).Get(0).number().floatValue() * scaleX;
    float y0 = waypoints.get(waypoints.length() - 1).Get(1).number().floatValue() * scaleY;
    for (int i = 0; i < waypoints.length(); i++) {
      float x1 = waypoints.get(i).Get(0).number().floatValue() * scaleX;
      float y1 = waypoints.get(i).Get(1).number().floatValue() * scaleY;
      Line2D line = new Line2D.Float(x0, max_y - y0, x1, max_y - y1);
      graphics.draw(line);
      x0 = x1;
      y0 = y1;
    }
    image = ImageFormat.from(buffImage);
  }

  @Override
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO;
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return pointcost(trajectory.get(trajectory.size() - 1).state());
  }

  /* package */ Scalar pointcost(Tensor tensor) {
    if (tensor.length() != 2)
      tensor = tensor.extract(0, 2);
    Tensor pixel = Floor.of(tensor.pmul(scale));
    // code features redundancies for instance to ImageRegion
    int pix = pixel.Get(0).number().intValue();
    if (0 <= pix && pix < DIMENSIONS.get(1)) {
      int piy = max_y - pixel.Get(1).number().intValue();
      if (0 <= piy && piy < DIMENSIONS.get(0))
        return image.Get(piy, pix);
    }
    return outside;
  }
}
