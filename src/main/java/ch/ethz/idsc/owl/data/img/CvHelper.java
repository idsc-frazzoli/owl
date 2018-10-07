// code by ynager
package ch.ethz.idsc.owl.data.img;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;

public enum CvHelper {
  ;
  /** @param bufferedImage grayscale
   * @return */
  public static Mat bufferedImageToMat(BufferedImage bufferedImage) {
    Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), opencv_core.CV_8U);
    byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
    mat.data().put(data);
    return mat;
  }
}
