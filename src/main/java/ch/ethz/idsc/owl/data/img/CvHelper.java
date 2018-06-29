package ch.ethz.idsc.owl.data.img;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.WindowConstants;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import ch.ethz.idsc.tensor.Tensor;

public enum CvHelper {
  ;
  public static Mat bufferedImageToMat(BufferedImage bi) {
    Mat mat = new Mat(bi.getHeight(), bi.getWidth(), opencv_core.CV_8U);
    byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
    mat.data().put(data);
    return mat;
  }

  public static void displayMat(Mat image, String caption) {
    final CanvasFrame canvas = new CanvasFrame(caption, 1.0);
    canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    final OpenCVFrameConverter<?> converter = new OpenCVFrameConverter.ToMat();
    canvas.showImage(converter.convert(image));
  }

  public static Point tensorToPoint(Tensor tensor) {
    int[] intArr = new int[tensor.length() * 2];
    for (int i = 0; i < tensor.length(); i++) {
      intArr[i * 2] = tensor.get(i).Get(0).number().intValue();
      intArr[i * 2 + 1] = tensor.get(i).Get(1).number().intValue();
    }
    Point point = new opencv_core.Point(intArr.length);
    point.put(intArr, 0, intArr.length);
    return point;
  }
}
