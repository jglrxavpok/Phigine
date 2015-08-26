package org.jglr.phiengine.core.entity

/**
 * Convenience class to allow for less typing while defining simple prototypes<br/>
 * @example
 * {{{
 *   object MyPrototype extends StaticProtype(MyComponentA.class, MyComponentB.class)
 * }}}
 */
class StaticPrototype(components: CompClass*) extends EntityPrototype {
  final override def staticComponents: Seq[CompClass] = components
}
