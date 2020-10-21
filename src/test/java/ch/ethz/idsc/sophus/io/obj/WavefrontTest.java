// code by jph
package ch.ethz.idsc.sophus.io.obj;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ch.ethz.idsc.sophus.math.MinMax;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.ext.ReadLine;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class WavefrontTest extends TestCase {
  private static void check3d(File file) throws IOException {
    try (InputStream inputStream = new FileInputStream(file)) {
      Wavefront wavefront = WavefrontFormat.parse(ReadLine.of(inputStream));
      Tensor normals = wavefront.normals();
      assertEquals(Dimensions.of(normals).get(1), Integer.valueOf(3));
      Tensor vertices = wavefront.vertices();
      assertEquals(Dimensions.of(vertices).get(1), Integer.valueOf(3));
      List<WavefrontObject> objects = wavefront.objects();
      for (WavefrontObject wavefrontObject : objects) {
        Tensor faces = wavefrontObject.faces();
        if (0 < faces.length()) {
          MinMax minMax = MinMax.of(faces);
          assertEquals(minMax.min().map(Sign::requirePositiveOrZero), minMax.min());
          ScalarUnaryOperator hi_bound = Min.function(RealScalar.of(vertices.length() - 1));
          assertEquals(minMax.max().map(hi_bound), minMax.max());
        }
        Tensor nrmls = wavefrontObject.normals();
        if (0 < nrmls.length()) {
          MinMax minMax = MinMax.of(nrmls);
          assertEquals(minMax.min().map(Sign::requirePositiveOrZero), minMax.min());
          ScalarUnaryOperator hi_bound = Min.function(RealScalar.of(normals.length() - 1));
          assertEquals(minMax.max().map(hi_bound), minMax.max());
        }
      }
    }
  }

  public void testLoad() throws IOException {
    File directory = HomeDirectory.file("Projects/gym-duckietown/gym_duckietown/meshes");
    if (directory.isDirectory())
      for (File file : directory.listFiles())
        if (file.getName().endsWith(".obj"))
          check3d(file);
  }
}
