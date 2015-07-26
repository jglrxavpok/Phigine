package org.jglr.phiengine.core.utils

object JavaConversions {
  import java.util.function.{ Function => JFunction, Predicate => JPredicate, BiPredicate, Consumer }
  import java.util.Comparator

  //usage example: `i: Int ? 42`
  implicit def toJavaFunction[A, B](f: (A) => B) = new JFunction[A, B] {
    override def apply(a: A): B = f(a)
  }

  //usage example: `i: Int ? true`
  implicit def toJavaPredicate[A](f: Function1[A, Boolean]) = new JPredicate[A] {
    override def test(a: A): Boolean = f(a)
  }

  //usage example: `(i: Int, s: String) ? true`
  implicit def toJavaBiPredicate[A, B](predicate: (A, B) => Boolean) =
    new BiPredicate[A, B] {
      def test(a: A, b: B) = predicate(a, b)
    }

  implicit def toJavaConsumer[A](consumer: (A) => Any) =
    new Consumer[A] {
      override def accept(t: A): Unit = consumer(t)
    }

  implicit def toJavaComparator[A](comparator: (A, A) => Int) =
    new Comparator[A] {
      override def compare(a: A, b: A): Int = comparator(a, b)
    }
}
