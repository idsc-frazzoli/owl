// code by jph
package ch.ethz.idsc.sophus.io.obj;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ReadLine;

/* package */ enum WavefrontFormatDemo {
  ;
  static void load(File file) throws IOException {
    try (InputStream inputStream = new FileInputStream(file)) {
      Wavefront wavefront = WavefrontFormat.parse(ReadLine.of(inputStream));
      Tensor normals = wavefront.normals();
      System.out.println("normals=" + Dimensions.of(normals));
      Tensor vertices = wavefront.vertices();
      System.out.println("vertices=" + Dimensions.of(vertices));
      List<WavefrontObject> objects = wavefront.objects();
      System.out.println("objects=" + objects.size());
    }
  }

  public static void main(String[] args) {
    File dir = HomeDirectory.file("Projects/gym-duckietown/gym_duckietown/meshes");
    for (File file : dir.listFiles())
      if (file.getName().endsWith(".obj"))
        try {
          load(file);
        } catch (Exception exception) {
          System.err.println(file);
          // ---
          exception.printStackTrace();
        }
  }
}
