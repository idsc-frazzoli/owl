// code by jph
package ch.ethz.idsc.sophus.app.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import ch.ethz.idsc.sophus.lie.se3.Se3Matrix;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.ext.ReadLine;
import ch.ethz.idsc.tensor.lie.Quaternion;
import ch.ethz.idsc.tensor.lie.QuaternionToRotationMatrix;

/** text file with values separated by space with first line as
 * <pre>
 * "# id timestamp tx ty tz qx qy qz qw"
 * </pre>
 * and columns as
 * <pre>
 * id timestamp tx ty tz qx qy qz qw
 * </pre>
 * 
 * Reference:
 * "Are We Ready for Autonomous Drone Racing? The UZH-FPV Drone Racing Dataset"
 * by J. Delmerico, T. Cieslewski, H. Rebecq, M. Faessler, D. Scaramuzza
 * 
 * UZH-FPV Drone Racing Dataset
 * http://rpg.ifi.uzh.ch/uzh-fpv.html */
public enum UzhSe3TxtFormat {
  ;
  /** @param file with extension txt
   * @return tensor of affine matrices
   * @throws IOException
   * @throws FileNotFoundException */
  public static Tensor of(File file) throws FileNotFoundException, IOException {
    try (InputStream inputStream = new FileInputStream(file)) {
      return Tensor.of(ReadLine.of(inputStream).skip(1).map(UzhSe3TxtFormat::parse));
    }
  }

  private static Tensor parse(String string) {
    Tensor vector = Tensor.of(Stream.of(string.split(" ")).map(Scalars::fromString));
    Quaternion quaternion = Quaternion.of(vector.Get(5), vector.Get(6), vector.Get(7), vector.Get(8));
    return Se3Matrix.of(QuaternionToRotationMatrix.of(quaternion), vector.extract(2, 5));
  }
}
