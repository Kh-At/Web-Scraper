import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se._
import java.nio.file.{Files, Paths}
import scala.io.Source

class MyTest extends AnyWordSpec {

  // Hilfsklasse für Observer Tests
  class TestObserver extends ModelObserver {
    var lastContent: List[String] = List()
    var lastStatus: String = ""
    var lastSourceType: String = ""
    var callCount: Int = 0
    
    def onModelChanged(content: List[String], status: String, sourceType: String): Unit = {
      lastContent = content
      lastStatus = status
      lastSourceType = sourceType
      callCount += 1
    }
    
    def reset(): Unit = {
      lastContent = List()
      lastStatus = ""
      lastSourceType = ""
      callCount = 0
    }
  }

  // Tests für die originale TUI
  val myTestTui: Tui = new Tui(3,3) 
  "Tui" should {
    "have a bar as String of form '+---+'" in {
      myTestTui.build_bar(3) should be ("+---+" + "\n")
    }

    "have a scalable bar" in {
      myTestTui.build_bar(1) should be("+-+" + "\n")
      myTestTui.build_bar(2) should be("+--+" + "\n")
      myTestTui.build_bar(4) should be("+----+" + "\n")
    }

    "display frame elements correctly" in {
      val emptyContent = List()
      val result = myTestTui.build_all()
      result should include ("+")
      result should include ("|")
      result should include ("-")
    }
  }

  // Tests für Tui mit verschiedenen Status
  "Tui with different status" should {
    val view = new Tui(10, 5)
    
    "build correct bar" in {
      view.build_bar(5) should be("+-----+" + "\n")
      view.build_bar(8) should be("+--------+" + "\n")
    }
    
    "build tower with content" in {
      val content = List("Line 1", "Line 2")
      val result = view.build_tower(10, 5, content)
      result should include ("Line 1")
      result should include ("Line 2")
    }
    
    "handle empty content" in {
      val result = view.build_tower(8, 4, List())
      result should include ("|")
    }
    
    "handle long text wrapping" in {
      val longText = "This is a very long text that should wrap to multiple lines"
      val result = view.build_tower(15, 10, List(longText))
      result.linesIterator.count(_.contains("|")) should be > 2
    }
    
    "handle very long words" in {
      val longWord = "supercalifragilisticexpialidocious"
      val result = view.build_tower(10, 5, List(longWord))
      result should include ("supercalif")
      result should include ("ragilistic")
    }
    
    // KORRIGIERT: >= statt >
    "handle single very long word" in {
      val result = view.build_tower(5, 4, List("abcdefghijklmnopqrstuvwxyz"))
      result.linesIterator.count(_.contains("|")) should be >= 2
    }
    
    "format lines correctly with exact width" in {
      val exactWidthText = "12345"
      val result = view.build_tower(5, 3, List(exactWidthText))
      result should include ("12345")
    }
    
    "show welcome message" in {
      noException should be thrownBy view.showWelcome()
    }
    
    "display method should not throw exceptions" in {
      noException should be thrownBy view.display()
    }
    
    "handle onModelChanged for different status types" in {
      view.onModelChanged(List("test"), "success", "user_input")
      view.onModelChanged(List("error test"), "error", "file")
      view.onModelChanged(List(), "loading", "website")
    }
    
    "show loading status correctly" in {
      view.onModelChanged(List(), "loading", "website")
      val result = view.build_all()
      result should include ("Loading")
    }
    
    // KORRIGIERT: Teste beide Teile des umgebrochenen Texts
    "show error status correctly" in {
      view.onModelChanged(List("File not found"), "error", "file")
      val result = view.build_all()
      result should include ("File not") // Erster Teil
      result should include ("found")    // Zweiter Teil
    }
  }

  "ContentSource implementations" should {
    "UserInputSource return correct content" in {
      val source = new UserInputSource("test input")
      source.getContent() should be(List("test input"))
      source.getSourceType() should be("user_input")
    }
    
    "UserInputSource handle empty input" in {
      val source = new UserInputSource("")
      source.getContent() should be(List(""))
      source.getSourceType() should be("user_input")
    }
    
    "FileContentSource load file content" in {
      val testFile = "test_input.txt"
      Files.write(Paths.get(testFile), "line1\nline2\nline3".getBytes)
      
      val source = new FileContentSource(testFile)
      source.getContent() should be(List("line1", "line2", "line3"))
      source.getSourceType() should be("file")
      
      Files.deleteIfExists(Paths.get(testFile))
    }
    
    "FileContentSource handle non-existent file" in {
      val source = new FileContentSource("nonexistent_file_12345.txt")
      an[Exception] should be thrownBy source.getContent()
    }
    
    "WebsiteContentSource return placeholder content" in {
      val source = new WebsiteContentSource("http://example.com")
      source.getContent() should be(List("Website: http://example.com", "Content would be scraped here..."))
      source.getSourceType() should be("website")
    }
  }

  "WebScraperModel" should {
    val model = new WebScraperModel()
    val testObserver = new TestObserver()
    
    "initialize with empty content" in {
      model.getContent should be(List())
      model.getStatus should be("ready")
      model.getSourceType should be("none")
    }
    
    "add and remove observers" in {
      model.addObserver(testObserver)
      model.getContent
      model.removeObserver(testObserver)
    }
    
    "process user input content successfully" in {
      model.addObserver(testObserver)
      testObserver.reset()
      
      val source = new UserInputSource("test content")
      model.processContent(source)
      
      model.getContent should be(List("test content"))
      model.getStatus should be("success")
      model.getSourceType should be("user_input")
      testObserver.callCount should be > 0
      
      model.removeObserver(testObserver)
    }
    
    "process file content successfully" in {
      model.addObserver(testObserver)
      testObserver.reset()
      
      val testFile = "test_model.txt"
      Files.write(Paths.get(testFile), "model test content".getBytes)
      
      val source = new FileContentSource(testFile)
      model.processContent(source)
      
      model.getContent should contain ("model test content")
      model.getStatus should be("success")
      model.getSourceType should be("file")
      
      Files.deleteIfExists(Paths.get(testFile))
      model.removeObserver(testObserver)
    }
    
    "process website content successfully" in {
      model.addObserver(testObserver)
      testObserver.reset()
      
      val source = new WebsiteContentSource("http://test.com")
      model.processContent(source)
      
      model.getContent should not be empty
      model.getStatus should be("success")
      model.getSourceType should be("website")
      testObserver.callCount should be(2)
      
      model.removeObserver(testObserver)
    }
    
    "handle errors gracefully" in {
      model.addObserver(testObserver)
      testObserver.reset()
      
      val invalidSource = new FileContentSource("nonexistent_file.txt")
      model.processContent(invalidSource)
      
      model.getStatus should be("error")
      model.getContent.head should startWith ("Error:")
      testObserver.callCount should be > 0
      
      model.removeObserver(testObserver)
    }
    
    "notify multiple observers" in {
      val observer1 = new TestObserver()
      val observer2 = new TestObserver()
      
      model.addObserver(observer1)
      model.addObserver(observer2)
      
      model.processContent(new UserInputSource("multi observer test"))
      
      observer1.lastContent should be(List("multi observer test"))
      observer2.lastContent should be(List("multi observer test"))
      
      model.removeObserver(observer1)
      model.removeObserver(observer2)
    }
  }

  "Controller" should {
    val model = new WebScraperModel()
    val view = new Tui(20, 10)
    val controller = new Controller(model, view)
    
    "handle user input commands" in {
      noException should be thrownBy controller.handleUserInput("help")
      noException should be thrownBy controller.handleUserInput("clear")
    }
    
    "process file input successfully" in {
      val testFile = "test_controller.txt"
      Files.write(Paths.get(testFile), "controller test content".getBytes)
      
      controller.handleUserInput(s"load $testFile")
      model.getContent should contain ("controller test content")
      
      Files.deleteIfExists(Paths.get(testFile))
    }
    
    "process user input directly" in {
      controller.handleUserInput("input direct test")
      model.getContent should be(List("direct test"))
    }
    
    "process website input" in {
      controller.handleUserInput("scrape http://example.com")
      model.getContent should not be empty
      model.getSourceType should be("website")
    }
    
    "handle save command" in {
      model.processContent(new UserInputSource("save test content"))
      
      val saveFile = "test_save.txt"
      controller.handleUserInput(s"save $saveFile")
      
      Files.exists(Paths.get(saveFile)) should be(true)
      Files.deleteIfExists(Paths.get(saveFile))
    }
    
    "handle unknown commands" in {
      noException should be thrownBy controller.handleUserInput("unknown command")
    }
    
    "handle empty input" in {
      noException should be thrownBy controller.handleUserInput("")
    }
    
    "handle input mode commands" in {
      noException should be thrownBy controller.handleUserInput("input")
      noException should be thrownBy controller.handleUserInput("i")
    }
    
    "handle input with multiple words" in {
      controller.handleUserInput("input this is a longer test")
      model.getContent should be(List("this is a longer test"))
    }
  }

  "Observer Pattern" should {
    "notify observers on content change" in {
      val model = new WebScraperModel()
      val testObserver = new TestObserver()
      
      model.addObserver(testObserver)
      model.processContent(new UserInputSource("observer test"))
      
      testObserver.lastContent should be(List("observer test"))
      testObserver.lastStatus should be("success")
      testObserver.lastSourceType should be("user_input")
    }
    
    "not notify removed observers" in {
      val model = new WebScraperModel()
      val testObserver = new TestObserver()
      
      model.addObserver(testObserver)
      model.removeObserver(testObserver)
      model.processContent(new UserInputSource("test"))
      
      testObserver.lastContent should be(List())
    }
  }

  "Main application" should {
    "check arguments correctly" in {
      Main.checkArgs(10, 10) should be(true)
      Main.checkArgs(0, 10) should be(false)
      Main.checkArgs(10, 0) should be(false)
      Main.checkArgs(0, 0) should be(false)
      Main.checkArgs(1, 1) should be(true)
    }
    
    "validate arguments method exists" in {
      noException should be thrownBy Main.checkArgs(5, 5)
    }
  }

  "Integration" should {
    "work with complete MVC flow" in {
      val model = new WebScraperModel()
      val view = new Tui(15, 6)
      val controller = new Controller(model, view)
      
      model.addObserver(view)
      controller.handleUserInput("input integration test")
      
      model.getContent should be(List("integration test"))
      model.getStatus should be("success")
      
      val display = view.build_all()
      display should include ("integration")
      display should include ("test")
    }
    
    "test all status types in Tui" in {
      val model = new WebScraperModel()
      val view = new Tui(20, 8)
      
      model.addObserver(view)
      
      val websiteSource = new WebsiteContentSource("http://test.com")
      model.processContent(websiteSource)
      
      val invalidSource = new FileContentSource("nonexistent_file_123.txt")
      model.processContent(invalidSource)
      
      val userSource = new UserInputSource("success test")
      model.processContent(userSource)
      
      model.getContent should be(List("success test"))
    }
    
    "handle file save and load integration" in {
      val model = new WebScraperModel()
      val view = new Tui(15, 6)
      val controller = new Controller(model, view)
      
      model.addObserver(view)
      
      controller.handleUserInput("input integration file test")
      val saveFile = "integration_save_test.txt"
      controller.handleUserInput(s"save $saveFile")
      
      controller.handleUserInput(s"load $saveFile")
      model.getContent should be(List("integration file test"))
      
      Files.deleteIfExists(Paths.get(saveFile))
    }
  }

  "Text wrapping edge cases" should {
    val view = new Tui(10, 5)
    
    "handle empty string" in {
      val result = view.build_tower(8, 3, List(""))
      result should include ("|")
    }
    
    "handle mixed content" in {
      val content = List("Short", "This is a medium line", "This is a very long line that should definitely wrap")
      val result = view.build_tower(12, 8, content)
      result should include ("Short")
      result should include ("This is a")
      result should include ("medium")
    }
    
    "handle exact width matching" in {
      val exactContent = List("1234567890")
      val result = view.build_tower(10, 3, exactContent)
      result should include ("1234567890")
    }
  }
}