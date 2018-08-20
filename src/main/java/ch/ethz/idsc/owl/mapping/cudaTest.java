package ch.ethz.idsc.owl.mapping;

import java.awt.image.BufferedImage;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.GpuMat;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_cudafilters;
import org.bytedeco.javacpp.opencv_cudafilters.Filter;
import org.bytedeco.javacpp.opencv_imgproc;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.data.img.CvHelper;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;

enum cudaTest {
  ;
  public static void main(String[] args) {
    Mat kernel = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_ELLIPSE, //
        new Size(2, 2));
    //
    Tensor image = ResourceData.of("/map/scenarios/s1/car_obs.png");
    BufferedImage bufferedImage = ImageFormat.of(image);
    Mat img = CvHelper.bufferedImageToMat(bufferedImage);
    // CPU dilate
    Mat dst = new Mat(img.size(), img.type());
    Stopwatch s = Stopwatch.started();
    opencv_imgproc.dilate(img, dst, kernel, new Point(-1, -1), 100, opencv_core.BORDER_CONSTANT, null);
    //opencv_imgproc.threshold(img, dst, 128.0, 255.0, opencv_imgproc.THRESH_BINARY);
    System.out.println(s.display_seconds());
    s.stop();
    s.resetToZero();
    // 
    // GPU dilate
    GpuMat src_g = new GpuMat(img.size(), img.type());
    GpuMat dst_g = new GpuMat(img.size(), img.type());
    src_g.upload(img);
    Filter filter = opencv_cudafilters.createMorphologyFilter(opencv_imgproc.MORPH_DILATE, src_g.type(), kernel, new Point(-1, -1), 100);
    s.start();
    filter.apply(src_g, dst_g);
    //opencv_cudaarithm.threshold(src_g, dst_g, 128.0, 255.0, opencv_imgproc.THRESH_BINARY);
    System.out.println(s.display_seconds());

  }
}
