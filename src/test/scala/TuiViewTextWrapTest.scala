import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.*

class TuiViewTextWrapTest extends AnyWordSpec {
  
  "Text wrapping" should {
    val view = new Tui(20, 10)
    
    "handle short text" in {
      // Da wrapText private ist, testen wir es indirekt über build_tower
      val result = view.build_tower(10, 5, List("short"))
      result should include ("short")
    }
    
    "handle long text by testing the output format" in {
      val longText = "This is a very long text that should definitely wrap to multiple lines in the display"
      val result = view.build_tower(15, 10, List(longText))
      
      // Sollte mehrere Zeilen produzieren
      val lineCount = result.count(_ == '\n')
      lineCount should be > 5
    }
    
    "maintain pipe alignment with wrapped text" in {
      val content = List("This text is long enough to wrap")
      val result = view.build_tower(12, 8, content)
      
      // Alle Zeilen sollten mit Pipe beginnen und enden
      val lines = result.split('\n')
      lines.filter(_.nonEmpty).foreach { line =>
        line should startWith ("|")
        line should endWith ("|")
      }
    }
  }
}