// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.io.File;
import java.util.Optional;

import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.TensorRank;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.io.ResourceData;

public enum ImageRegions {
  ;
  /** @param path of image resource in repo
   * @param range vector of length 2
   * @param strict
   * @return */
  public static Region<Tensor> loadFromRepository(String path, Tensor range, boolean strict) {
    return _universal(ResourceData.of(path), range, strict);
  }

  /** @param file on local machine
   * @param range vector of length 2
   * @param strict
   * @return */
  public static ImageRegion loadFromLocalFile(File file, Tensor range, boolean strict) throws Exception {
    return _universal(Import.of(file), range, strict);
  }

  private static ImageRegion _universal(Tensor image, Tensor range, boolean strict) {
    return new ImageRegion(grayscale(image), range, strict);
  }

  /** grayscale images that encode free space (as black pixels) and the location of obstacles
   * (as non-black pixels) may be stored as grayscale, or indexed color images. Images with
   * colors palette may be of smaller size than the equivalent grayscale image. Indexed color
   * images have RGBA channels. The function converts the given image to a grayscale image if
   * necessary.
   * 
   * @param image
   * @return matrix with entries from the range {0, 1, ..., 255}
   * @throws Exception if input does not represent an image */
  public static Tensor grayscale(Tensor image) {
    Optional<Integer> optional = TensorRank.ofArray(image);
    switch (optional.get()) {
    case 2:
      return image.copy();
    case 3:
      return image.get(Tensor.ALL, Tensor.ALL, 0); // take RED channel for region member test
    }
    throw TensorRuntimeException.of(image);
  }
}
