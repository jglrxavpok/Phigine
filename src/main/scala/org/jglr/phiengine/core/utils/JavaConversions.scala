package org.jglr.phiengine.core.utils

import java.util.function.{ Function => JFunction, Predicate => JPredicate, BiPredicate, Consumer }
import java.util.Comparator

/**
 * Object used to implicitly convert from Scala's Functions to Java's Function, Predicate, Consumer and BiPredicate
 */
object JavaConversions {

  implicit def toScalaFunction[A, B](f: JFunction[A, B]) = {
    a: A =>
      f.apply(a)
  }

  implicit def toJavaFunction[A, B](f: (A) => B) = new JFunction[A, B] {
    override def apply(a: A): B = f(a)
  }

  implicit def toJavaPredicate[A](f: (A) => Boolean) = new JPredicate[A] {
    override def test(a: A): Boolean = f(a)
  }

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
