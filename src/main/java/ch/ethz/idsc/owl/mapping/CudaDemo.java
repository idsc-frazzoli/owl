// code by ynager
package ch.ethz.idsc.owl.mapping;

import java.awt.image.BufferedImage;

import javax.swing.WindowConstants;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.GpuMat;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_cudafilters;
import org.bytedeco.javacpp.opencv_cudafilters.Filter;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import ch.ethz.idsc.owl.data.img.CvHelper;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.io.Timing;

/* package */ enum CudaDemo {
  ;
  private static void displayMat(Mat image, String caption) {
    CanvasFrame canvas = new CanvasFrame(caption, 1.0);
    canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    OpenCVFrameConverter<?> converter = new OpenCVFrameConverter.ToMat();
    canvas.showImage(converter.convert(image));
  }

  public static void main(String[] args) {
    int radius = 4;
    Mat kernel = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_ELLIPSE, //
        new Size(2 * radius, 2 * radius));
    int it = 1;
    //
    Tensor image = ResourceData.of("/simulation/s1/ped_obs_legal.png");
    BufferedImage bufferedImage = ImageFormat.of(image);
    Mat src = CvHelper.bufferedImageToMat(bufferedImage);
    // opencv_imgproc.resize(src, src, new Size(2*src.arrayWidth(), 2*src.arrayHeight()));
    //
    // CPU dilate
    Mat dst = new Mat(src.size(), src.type());
    Timing timing = Timing.started();
    opencv_imgproc.dilate(src, dst, kernel, new Point(-1, -1), it, opencv_core.BORDER_CONSTANT, null);
    // opencv_imgproc.threshold(img, dst, 128.0, 255.0, opencv_imgproc.THRESH_BINARY);
    System.out.println("CPU: " + timing.seconds());
    displayMat(dst, "CPU");
    timing = Timing.started();
    //
    // GPU dilate
    GpuMat src_g = new GpuMat(src.size(), src.type());
    GpuMat dst_g = new GpuMat(src.size(), src.type());
    src_g.upload(src);
    Filter filter = opencv_cudafilters.createMorphologyFilter(opencv_imgproc.MORPH_DILATE, src_g.type(), kernel, new Point(-1, -1), it);
    timing.stop();
    filter.apply(src_g, dst_g);
    // opencv_cudaarithm.threshold(src_g, dst_g, 128.0, 255.0, opencv_imgproc.THRESH_BINARY);
    System.out.println("GPU: " + timing.seconds());
    timing = Timing.started();
    dst_g.download(dst);
    displayMat(dst, "GPU");
    //
    // CPU DT dilate
    Mat negSrc = new Mat();
    Mat negSrcDT = new Mat();
    Mat dilated = new Mat();
    Mat rad = new Mat(Scalar.all(it * radius));
    timing.start();
    opencv_core.bitwise_not(src, negSrc);
    opencv_imgproc.distanceTransform(negSrc, negSrcDT, opencv_imgproc.CV_DIST_L2, opencv_imgproc.CV_DIST_MASK_PRECISE);
    opencv_core.compare(negSrcDT, rad, dilated, opencv_core.CMP_LE);
    timing.stop();
    displayMat(dilated, "DT CPU");
    System.out.println("DT CPU: " + timing.seconds());
    //
  }
}
