package org.jglr.phiengine.core.utils

object JavaConversions {
  import java.util.function.{ Function => JFunction, Predicate => JPredicate, BiPredicate, Consumer }

  //usage example: `i: Int ? 42`
  implicit def toJavaFunction[A, B](f: Function1[A, B]) = new JFunction[A, B] {
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

  implicit def toJavaConsumer0[A](consumer: (A) => Any) =
    new Consumer[A] {
      override def accept(t: A): Unit = consumer(t)
    }

}
