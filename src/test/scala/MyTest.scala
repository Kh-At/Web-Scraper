import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.*

class MyTest extends AnyWordSpec {

  val myTestTui: Tui = Tui(3,3) 
  "Tui" should {
    "have a bar as String of form '+---+'" in (
      myTestTui.build_bar(3) should be ("+---+" + "\n")
    )

    "have a scalable bar" in {
      myTestTui.build_bar(1) should be("+-+" + "\n")
      myTestTui.build_bar(2) should be("+--+" + "\n")
      myTestTui.build_bar(4) should be("+----+" + "\n")
    }

    "have a tower as string of form '|   |'" in {
      myTestTui.build_tower(3,3) should startWith ("|   |"+ "\n" + "|   |" + "\n" + "|   |" + "\n" )
    }
    
    "have a scalable tower" in {
      myTestTui.build_tower(3,1) should startWith ("|   |" + "\n") 
      myTestTui.build_tower(3,2) should startWith ("|   |" + "\n" + "|   |" + "\n") 
      myTestTui.build_tower(3,4) should startWith ("|   |" + "\n" + "|   |" + "\n" + "|   |" + "\n" + "|   |" + "\n")
    }
    
    "total should be of form " in {
      myTestTui.build_all() should be(
      "+---+" + "\n" +
      "|   |" + "\n" +
      "|   |" + "\n" +
      "|   |" + "\n" +
      "+---+" + "\n" )
    }
  }
}