// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.io.File;

import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.TensorRank;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.io.ResourceData;

public enum ImageRegions {
  ;
  private static ImageRegion _universal(Tensor image, Tensor range, boolean strict) {
    if (TensorRank.of(image) == 3) // the rank of images with a color palette is 3
      image = image.get(Tensor.ALL, Tensor.ALL, 0); // take RED channel for region member test
    return new ImageRegion(image, range, strict);
  }

  // for files in repo
  public static ImageRegion loadFromRepository(String string, Tensor range, boolean strict) throws Exception {
    return _universal(ResourceData.of(string), range, strict);
  }

  // for files on local machine
  public static ImageRegion loadFromLocalFile(File file, Tensor range, boolean strict) throws Exception {
    return _universal(Import.of(file), range, strict);
  }
}
