// code by jph
package ch.ethz.idsc.sophus.io.obj;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.ext.ReadLine;
import ch.ethz.idsc.tensor.red.Max;
import junit.framework.TestCase;

public class WavefrontFormatTest extends TestCase {
  public void testBlender0() throws IOException {
    try (InputStream inputStream = getClass().getResource("/io/obj/blender0.obj").openStream()) {
      Wavefront wavefront = WavefrontFormat.parse(ReadLine.of(inputStream));
      assertEquals(wavefront.objects().size(), 2);
      assertEquals(wavefront.objects().get(0).name(), "Cylinder");
      assertEquals(wavefront.objects().get(1).name(), "Cube");
      assertEquals(Dimensions.of(wavefront.vertices()), Arrays.asList(72, 3));
      assertTrue(MatrixQ.of(wavefront.normals()));
      assertEquals(Dimensions.of(wavefront.normals()), Arrays.asList(40, 3));
      List<WavefrontObject> objects = wavefront.objects();
      assertEquals(objects.size(), 2);
      {
        WavefrontObject wavefrontObject = objects.get(0);
        List<Integer> list = wavefrontObject.faces().stream() //
            .map(Tensor::length).distinct().collect(Collectors.toList());
        // contains quads and top/bottom polygon
        assertEquals(list, Arrays.asList(4, 32));
        Tensor normals = wavefrontObject.normals();
        Tensor faces = wavefrontObject.faces();
        normals.add(faces); // test if tensors have identical structure
        assertTrue(ExactTensorQ.of(faces));
        assertTrue(ExactTensorQ.of(normals));
      }
      {
        WavefrontObject wavefrontObject = objects.get(1);
        List<Integer> list = wavefrontObject.faces().stream() //
            .map(Tensor::length).distinct().collect(Collectors.toList());
        assertEquals(list, Arrays.asList(4));
        assertTrue(MatrixQ.of(wavefront.normals()));
        Tensor normals = wavefrontObject.normals();
        Tensor faces = wavefrontObject.faces();
        normals.add(faces); // test if tensors have identical structure
        Scalar index_max = (Scalar) normals.flatten(-1).reduce(Max::of).get();
        assertEquals(index_max.number().intValue() + 1, wavefront.normals().length());
      }
    }
  }

  public void testMathematica0() throws IOException {
    try (InputStream inputStream = getClass().getResource("/io/obj/meshregionex2d.obj").openStream()) {
      Wavefront wavefront = WavefrontFormat.parse(ReadLine.of(inputStream));
      List<WavefrontObject> objects = wavefront.objects();
      assertEquals(objects.size(), 1);
      WavefrontObject wavefrontObject = objects.get(0);
      Tensor expect = Tensors.fromString("{{0, 1, 2}, {0, 1, 3}, {0, 1, 4}}");
      assertEquals(wavefrontObject.faces(), expect);
      Tensor vert = Tensors.fromString("{{0, 0, 0}, {0, 0, 1}, {0, -1, 0}, {1, 0, 0}, {0, 1, 0}}");
      assertEquals(wavefront.vertices(), vert);
    }
  }
}
