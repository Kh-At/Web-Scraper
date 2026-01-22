case class Spider(links: Int) {
  def isSet: Boolean = links != 0
}

val spidy1 = new Spider(2)
spidy1.isSet

val spidy2 = new Spider(0)
spidy2.isSet

case class Field(spidys: Array[Spider])

val field1 = Field(Array.ofDim[Spider](1))
field1.spidys(0) = spidy1