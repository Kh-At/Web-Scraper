import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.Tui.*
class MyTest extends AnyWordSpec {

  "Tui" should {
    "have a bar as String of form ' ---'" in (
      build_bar(3) should be (" ---" + eol)
    )

    "have a scalable bar" in {
      build_bar(1) should be("+-+" + eol)
      build_bar(2) should be("+--+" + eol)
      build_bar(4) should be("+----+" + eol)
    }

    "have a tower as string of form '|   |'" in (
      build_tower(3,3) should be ("|   | "+ "\n" + "|   |" + "\n" + "|   |" + "\n" + eol)
    )
    
    "have a scalable tower" in (
      build_tower(3,1) should be ("|   | "+ "\n") 
        build_tower(3,2) should be ("|   | "+ "\n" + "|   |" + "\n") 
        build_tower(3,4) should be ("|   | "+ "\n" + "|   | "+ "\n" + "|   |" + "\n" + "|   |" + "\n")
      )
    
    "total should be of form " in (
      build_all(10,6) should be(
      "+----------+" + "\n" +
      "|          |" + "\n" +
      "|          |" + "\n" +
      "|          |" + "\n" +
      "|          |" + "\n" +
      "|          |" + "\n" +
      "|          |" + "\n" +
      "+----------+" + "\n")
    )
  }
}
