//// code by ob
// package ch.ethz.idsc.sophus.filter;
//
// import ch.ethz.idsc.owl.data.BoundedLinkedList;
// import ch.ethz.idsc.sophus.math.SmoothingKernel;
// import ch.ethz.idsc.tensor.RationalScalar;
// import ch.ethz.idsc.tensor.RealScalar;
// import ch.ethz.idsc.tensor.Scalar;
// import ch.ethz.idsc.tensor.Tensor;
// import ch.ethz.idsc.tensor.Tensors;
// import ch.ethz.idsc.tensor.alg.Last;
// import ch.ethz.idsc.tensor.red.Total;
// import ch.ethz.idsc.tensor.sca.Chop;
//
// public class NonuniformMaskGenerator() {
// /** @param time stamp of control sequence
// * @return affine combination used to generate mask
// * @throws Exception if mask is not a vector or empty */
//
//
//
// //TODO OB
// private static BoundedLinkedList<Tensor> boundedLinkedList;
//
// public NonuniformMaskGenerator(int length) {
// this.boundedLinkedList = new BoundedLinkedList<>(length);
//
// }
//
// public static Tensor fixedLength(Tensor time, int length) {
// Tensor weight = Tensors.empty();
// Tensor Delta = boundedLinkedList.getFirst().subtract(boundedLinkedList.getLast());
// Scalar delta = Delta.Get(0);
//
// for(int index = 0; index < boundedLinkedList.size(); index++) {
// Scalar conversion = boundedLinkedList.get(index).subtract(boundedLinkedList.getFirst()).divide(delta.add(delta)).subtract(RationalScalar.HALF);
// weight.append(SmoothingKernel.GAUSSIAN.apply(conversion));
// }
// return weight;
// }
//
// public static Tensor fixedInterval(Tensor time, Scalar interval) {
//
//
// Tensor affine = Tensors.empty();
// return affine;
// }
// }
