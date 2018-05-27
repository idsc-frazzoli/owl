// code by jph
package ch.ethz.idsc.owl.gui.region;

import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.math.region.ImageRegion;

public enum ImageRegionRender {
  ;
  public static RenderInterface create(ImageRegion imageRegion) {
    return new ImageRender(RegionRenders.image(imageRegion.image()), imageRegion.scale());
  }
}
