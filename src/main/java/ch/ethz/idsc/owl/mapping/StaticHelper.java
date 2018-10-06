// code by ynager
package ch.ethz.idsc.owl.mapping;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.util.List;
import java.util.function.BiConsumer;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_imgproc;

import ch.ethz.idsc.tensor.Tensor;

/* package */ enum StaticHelper {
  ;
  static void makeStroke(Area area, float radius, BiConsumer<Area, Area> function) {
    Stroke stroke = new BasicStroke(radius * 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
    Shape strokeShape = stroke.createStrokedShape(area);
    Area strokeArea = new Area(strokeShape);
    function.accept(area, strokeArea);
  }

  static Mat dilateSegment(int s, Mat region, List<Mat> kernels, Point kernCenter, List<Mat> masks, int iterations) {
    Mat updatedSegment = new Mat(region.size(), region.type());
    opencv_core.bitwise_and(region, masks.get(s), updatedSegment);
    // opencv_cudaarithm.bitwise_and(region, masks.get(s), updatedSegment);
    opencv_imgproc.dilate(updatedSegment, updatedSegment, kernels.get(s), kernCenter, iterations, opencv_core.BORDER_CONSTANT, null);
    return updatedSegment;
  }

  static byte[] toAGRB(Color color) {
    return new byte[] { //
        (byte) color.getAlpha(), //
        (byte) color.getGreen(), //
        (byte) color.getRed(), //
        (byte) color.getBlue() };
  }

  /** @param tensor of the form {{x1, y1}, {x2, y2}, ..., {xn, yn}}
   * @return */
  static Point tensorToPoint(Tensor tensor) {
    int[] data = new int[tensor.length() * 2];
    int index = -1;
    for (Tensor point : tensor) {
      data[++index] = point.Get(0).number().intValue();
      data[++index] = point.Get(1).number().intValue();
    }
    Point point = new opencv_core.Point(data.length);
    point.put(data, 0, data.length);
    return point;
  }
}
