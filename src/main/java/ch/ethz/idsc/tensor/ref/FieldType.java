// code by jph
package ch.ethz.idsc.tensor.ref;

import java.io.File;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.StringScalarQ;

public enum FieldType {
  STRING(String.class::equals) {
    @Override
    public Object toObject(Class<?> cls, String string) {
      return string;
    }
  },
  BOOLEAN(Boolean.class::equals) {
    @Override
    public Object toObject(Class<?> cls, String string) {
      return BooleanParser.orNull(string);
    }
  },
  ENUM(Enum.class::isAssignableFrom) {
    @Override
    public Object toObject(Class<?> cls, String string) {
      return Stream.of(cls.getEnumConstants()) //
          .filter(object -> ((Enum<?>) object).name().equals(string)) //
          .findFirst() //
          .orElse(null);
    }
  },
  FILE(File.class::equals) {
    @Override
    public Object toObject(Class<?> cls, String string) {
      return new File(string);
    }
  },
  TENSOR(Tensor.class::equals) {
    @Override
    public Object toObject(Class<?> cls, String string) {
      return Tensors.fromString(string);
    }

    @Override
    boolean isValidValue(Object object) {
      return object instanceof Tensor //
          && !StringScalarQ.any((Tensor) object);
    }
  },
  SCALAR(Scalar.class::equals) {
    @Override
    public Object toObject(Class<?> cls, String string) {
      return Scalars.fromString(string);
    }

    @Override
    boolean isValidValue(Object object) {
      return object instanceof Scalar //
          && !StringScalarQ.of((Scalar) object);
    }
  }, //
  ;

  private final Predicate<Class<?>> predicate;

  private FieldType(Predicate<Class<?>> predicate) {
    this.predicate = predicate;
  }

  /* package */ final boolean isTracking(Class<?> cls) {
    return predicate.test(cls);
  }

  /* package */ abstract Object toObject(Class<?> cls, String string);

  /* package */ boolean isValidValue(Object object) {
    return Objects.nonNull(object) //
        && predicate.test(object.getClass()); // default implementation
  }

  /* package */ static String toString(Class<?> cls, Object object) {
    return Enum.class.isAssignableFrom(cls) //
        ? ((Enum<?>) object).name()
        : object.toString();
  }
}