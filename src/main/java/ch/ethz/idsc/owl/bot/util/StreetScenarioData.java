// code by jph
package ch.ethz.idsc.owl.bot.util;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.r2.ImageEdges;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;

public class StreetScenarioData {
  public static StreetScenarioData load(String id) {
    return new StreetScenarioData(id);
  }

  // ---
  /** rgba */
  public final BufferedImage render;
  /** bw */
  public final Tensor imagePedLegal;
  /** bw */
  public final Tensor imagePedIllegal;
  /** bw */
  private final Tensor imageCar;
  /** bw obstacles detected by lidar and creating occlusions */
  public final Tensor imageLid;
  /** string to resource with */
  public final String imageLanesString;
  // public final Tensor imageLanes;

  private StreetScenarioData(String id) {
    final String prefix = "/simulation/" + id + "/";
    render = bufferedImage(prefix + "render.png");
    imagePedLegal = ResourceData.of(prefix + "ped_obs_legal.png");
    imagePedIllegal = ResourceData.of(prefix + "ped_obs_illegal.png");
    imageCar = ResourceData.of(prefix + "car_obs_1.png");
    imageLid = ResourceData.of(prefix + "lidar_obs.png");
    imageLanesString = prefix + "car_lanes.png";
  }

  public Tensor imageCar_extrude(int width) {
    return ImageEdges.extrusion(imageCar, width);
  }

  // TODO JAN tensor v060
  private static BufferedImage bufferedImage(String string) {
    try (InputStream inputStream = ResourceData.class.getResourceAsStream(string)) {
      return ImageIO.read(inputStream);
    } catch (Exception exception) {
      // ---
    }
    return null;
  }
}
