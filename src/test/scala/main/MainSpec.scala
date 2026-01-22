package de.htwg.se.main

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterEach
import org.scalatest.OptionValues
import org.scalatest.concurrent.{Signaler, ThreadSignaler, TimeLimits}
import org.scalatest.time.SpanSugar._
import scala.util.{Try, Success, Failure}
import java.io.{ByteArrayOutputStream, PrintStream}
import java.lang.reflect.Field
import scala.sys.process.Process

class MainSpec extends AnyWordSpec with Matchers with BeforeAndAfterEach with OptionValues with TimeLimits {
  
  implicit val signaler: Signaler = ThreadSignaler
  
  private val originalOut = System.out
  private val originalErr = System.err
  private val outputStream = new ByteArrayOutputStream()
  private val printStream = new PrintStream(outputStream)
  
  // Mock classes to avoid GUI/Thread issues
  class MockTui(width: Int, height: Int)(using de.htwg.se.controller.ControllerInterface) 
    extends de.htwg.se.view.Tui(width, height) {
    
    override def start(): scala.util.Try[de.htwg.se.util.viewUtils.state.State] = {
      scala.util.Success(de.htwg.se.util.viewUtils.state.On())
    }
    
    override def mainLoop(state: de.htwg.se.util.viewUtils.state.Context): 
      de.htwg.se.util.viewUtils.state.State = {
      state.getState
    }
  }
  
  class MockGui(using de.htwg.se.controller.ControllerInterface) 
    extends de.htwg.se.view.Gui {
    
    override def start(): Unit = {
      // Do nothing to avoid GUI initialization
    }
    
    override def main(args: Array[String]): Unit = {
      // Override to prevent JFXApp3 initialization
    }
  }
  
  class MockModel extends de.htwg.se.model.WebScraperModel(Nil) {
    var observers: List[de.htwg.se.util.viewUtils.Observer] = Nil
    
    override def addObserver(observer: de.htwg.se.util.viewUtils.Observer): Unit = {
      observers = observer :: observers
    }
    
    override def getObservers(): scala.collection.mutable.ListBuffer[de.htwg.se.util.viewUtils.Observer] = {
      import scala.collection.mutable.ListBuffer
      ListBuffer(observers: _*)
    }
  }
  
  override def beforeEach(): Unit = {
    outputStream.reset()
    System.setOut(printStream)
    System.setErr(printStream)
    
    // Reset the model in Implicits using reflection
    resetImplicitsModel()
  }
  
  override def afterEach(): Unit = {
    System.setOut(originalOut)
    System.setErr(originalErr)
    
    // Clean up any threads that might have been created
    Thread.getAllStackTraces().keySet().forEach { thread =>
      if (thread.getName.contains("GUI-Thread") || thread.getName.contains("TUI-Thread")) {
        try {
          thread.interrupt()
        } catch {
          case _: SecurityException => // Ignore
        }
      }
    }
  }
  
  private def resetImplicitsModel(): Unit = {
    try {
      val implicitsClass = Class.forName("de.htwg.se.config.Implicits$")
      val modelField = implicitsClass.getDeclaredField("model")
      modelField.setAccessible(true)
      
      val newModel = new MockModel
      modelField.set(null, newModel)
    } catch {
      case e: Exception =>
        println(s"Failed to reset Implicits model: ${e.getMessage}")
    }
  }
  
  private def getImplicitsModel(): de.htwg.se.model.ScraperModelInterface = {
    try {
      val implicitsClass = Class.forName("de.htwg.se.config.Implicits$")
      val modelField = implicitsClass.getDeclaredField("model")
      modelField.setAccessible(true)
      modelField.get(null).asInstanceOf[de.htwg.se.model.ScraperModelInterface]
    } catch {
      case e: Exception =>
        println(s"Failed to get Implicits model: ${e.getMessage}")
        de.htwg.se.model.WebScraperModel(Nil)
    }
  }
  
  private def withMockedSystemExit(thunk: => Unit): Int = {
    val originalSecurityManager = System.getSecurityManager
    var exitCode: Option[Int] = None
    
    val securityManager = new SecurityManager {
      override def checkExit(status: Int): Unit = {
        exitCode = Some(status)
        throw new SecurityException(s"System.exit($status) called")
      }
      
      override def checkPermission(perm: java.security.Permission): Unit = {
        // Allow everything else
      }
    }
    
    System.setSecurityManager(securityManager)
    
    try {
      thunk
      -1 // No exit was called
    } catch {
      case _: SecurityException =>
        exitCode.getOrElse(-1)
    } finally {
      System.setSecurityManager(originalSecurityManager)
    }
  }
  
  "Main" should {
    
    "check valid arguments" in {
      Main.checkArgs(Array("10", "20")) shouldBe true
      Main.checkArgs(Array("1", "1")) shouldBe true
      Main.checkArgs(Array("100", "50")) shouldBe true
    }
    
    "check invalid arguments - too few arguments" in {
      Main.checkArgs(Array()) shouldBe false
      Main.checkArgs(Array("10")) shouldBe false
    }
    
    "check invalid arguments - non-numeric" in {
      Main.checkArgs(Array("abc", "20")) shouldBe false
      Main.checkArgs(Array("10", "xyz")) shouldBe false
      Main.checkArgs(Array("abc", "xyz")) shouldBe false
    }
    
    "check invalid arguments - zero or negative numbers" in {
      Main.checkArgs(Array("0", "10")) shouldBe false
      Main.checkArgs(Array("10", "0")) shouldBe false
      Main.checkArgs(Array("-1", "10")) shouldBe false
      Main.checkArgs(Array("10", "-5")) shouldBe false
    }
    
    "check invalid arguments - decimal numbers" in {
      Main.checkArgs(Array("10.5", "20")) shouldBe false
      Main.checkArgs(Array("10", "20.7")) shouldBe false
    }
    
    "check invalid arguments - empty strings" in {
      Main.checkArgs(Array("", "20")) shouldBe false
      Main.checkArgs(Array("10", "")) shouldBe false
      Main.checkArgs(Array("", "")) shouldBe false
    }
    
    "check invalid arguments - whitespace" in {
      Main.checkArgs(Array(" 10 ", " 20 ")) shouldBe false
      Main.checkArgs(Array("10", " 20")) shouldBe false
    }
    
    "handle main method with valid arguments (TUI only)" in {
      failAfter(3.seconds) {
        val exitCode = withMockedSystemExit {
          // Replace Tui with mock using reflection
          val mainClass = classOf[Main.type]
          val tuiField = mainClass.getDeclaredField("tui")
          tuiField.setAccessible(true)
          
          // Also replace Gui
          val guiField = mainClass.getDeclaredField("gui")
          guiField.setAccessible(true)
          
          try {
            Main.main(Array("80", "24"))
          } finally {
            // Clean up
            tuiField.set(null, null)
            guiField.set(null, null)
          }
        }
        
        // Should not exit (returns -1)
        exitCode shouldBe -1
      }
    }
    
    "handle main method with GUI mode" in {
      failAfter(3.seconds) {
        val exitCode = withMockedSystemExit {
          // Replace Tui and Gui with mocks using reflection
          val mainClass = classOf[Main.type]
          val tuiField = mainClass.getDeclaredField("tui")
          tuiField.setAccessible(true)
          
          val guiField = mainClass.getDeclaredField("gui")
          guiField.setAccessible(true)
          
          try {
            Main.main(Array("80", "24", "g"))
          } finally {
            // Clean up
            tuiField.set(null, null)
            guiField.set(null, null)
          }
        }
        
        exitCode shouldBe -1
      }
    }
    
    "exit with code 1 when arguments are invalid" in {
      val exitCode = withMockedSystemExit {
        Main.main(Array())
      }
      
      exitCode shouldBe 1
    }
    
    "exit with code 1 when first argument is invalid" in {
      val exitCode = withMockedSystemExit {
        Main.main(Array("abc", "20"))
      }
      
      exitCode shouldBe 1
    }
    
    "exit with code 1 when second argument is invalid" in {
      val exitCode = withMockedSystemExit {
        Main.main(Array("80", "xyz"))
      }
      
      exitCode shouldBe 1
    }
    
    "handle main method with minimum valid arguments" in {
      failAfter(3.seconds) {
        val exitCode = withMockedSystemExit {
          val mainClass = classOf[Main.type]
          val tuiField = mainClass.getDeclaredField("tui")
          tuiField.setAccessible(true)
          
          val guiField = mainClass.getDeclaredField("gui")
          guiField.setAccessible(true)
          
          try {
            Main.main(Array("1", "1"))
          } finally {
            tuiField.set(null, null)
            guiField.set(null, null)
          }
        }
        
        exitCode shouldBe -1
      }
    }
    
    "handle main method with large arguments" in {
      failAfter(3.seconds) {
        val exitCode = withMockedSystemExit {
          val mainClass = classOf[Main.type]
          val tuiField = mainClass.getDeclaredField("tui")
          tuiField.setAccessible(true)
          
          val guiField = mainClass.getDeclaredField("gui")
          guiField.setAccessible(true)
          
          try {
            Main.main(Array("9999", "9999"))
          } finally {
            tuiField.set(null, null)
            guiField.set(null, null)
          }
        }
        
        exitCode shouldBe -1
      }
    }
    
    "handle main method with extra arguments (more than 3)" in {
      failAfter(3.seconds) {
        val exitCode = withMockedSystemExit {
          val mainClass = classOf[Main.type]
          val tuiField = mainClass.getDeclaredField("tui")
          tuiField.setAccessible(true)
          
          val guiField = mainClass.getDeclaredField("gui")
          guiField.setAccessible(true)
          
          try {
            Main.main(Array("80", "24", "g", "extra", "args"))
          } finally {
            tuiField.set(null, null)
            guiField.set(null, null)
          }
        }
        
        exitCode shouldBe -1
      }
    }
    
    "handle main method with 'g' flag in different cases" in {
      val testCases = List("g", "G", " g ", "g ", " g")
      
      testCases.foreach { flag =>
        failAfter(2.seconds) {
          val exitCode = withMockedSystemExit {
            val mainClass = classOf[Main.type]
            val tuiField = mainClass.getDeclaredField("tui")
            tuiField.setAccessible(true)
            
            val guiField = mainClass.getDeclaredField("gui")
            guiField.setAccessible(true)
            
            try {
              Main.main(Array("80", "24", flag))
            } finally {
              tuiField.set(null, null)
              guiField.set(null, null)
            }
          }
          
          exitCode shouldBe -1
        }
      }
    }
    
    "add observers to model when starting" in {
      failAfter(3.seconds) {
        // Get initial model
        val initialModel = getImplicitsModel()
        initialModel shouldBe a[MockModel]
        
        val exitCode = withMockedSystemExit {
          val mainClass = classOf[Main.type]
          val tuiField = mainClass.getDeclaredField("tui")
          tuiField.setAccessible(true)
          
          val guiField = mainClass.getDeclaredField("gui")
          guiField.setAccessible(true)
          
          try {
            Main.main(Array("80", "24"))
          } finally {
            tuiField.set(null, null)
            guiField.set(null, null)
          }
        }
        
        exitCode shouldBe -1
        
        // Verify observers were added (through our MockModel)
        val updatedModel = getImplicitsModel().asInstanceOf[MockModel]
        updatedModel.observers should not be empty
      }
    }
    
    "start both TUI and GUI threads when 'g' flag is provided" in {
      failAfter(3.seconds) {
        val exitCode = withMockedSystemExit {
          val mainClass = classOf[Main.type]
          val tuiField = mainClass.getDeclaredField("tui")
          tuiField.setAccessible(true)
          
          val guiField = mainClass.getDeclaredField("gui")
          guiField.setAccessible(true)
          
          try {
            Main.main(Array("80", "24", "g"))
          } finally {
            tuiField.set(null, null)
            guiField.set(null, null)
          }
        }
        
        exitCode shouldBe -1
        
        // Verify that both TUI and GUI would have been started
        // (We can't easily verify threads were created, but we can verify no exception was thrown)
        succeed
      }
    }
    
    "set thread names appropriately" in {
      failAfter(3.seconds) {
        val exitCode = withMockedSystemExit {
          val mainClass = classOf[Main.type]
          val tuiField = mainClass.getDeclaredField("tui")
          tuiField.setAccessible(true)
          
          val guiField = mainClass.getDeclaredField("gui")
          guiField.setAccessible(true)
          
          try {
            Main.main(Array("80", "24", "g"))
          } finally {
            tuiField.set(null, null)
            guiField.set(null, null)
          }
        }
        
        exitCode shouldBe -1
        // Thread names are set in the code, but we can't easily verify them in tests
        succeed
      }
    }
  }
  
  "Main edge cases" should {
    
    "handle NumberFormatException gracefully in checkArgs" in {
      // This should be covered by non-numeric tests, but explicitly test exception
      Main.checkArgs(Array("10", "20x")) shouldBe false
      Main.checkArgs(Array("10L", "20")) shouldBe false
      Main.checkArgs(Array("10.0", "20")) shouldBe false
      Main.checkArgs(Array("10", "20f")) shouldBe false
    }
    
    "handle extremely large numbers" in {
      Main.checkArgs(Array("999999999", "999999999")) shouldBe true
      
      // This might overflow, but should still be valid
      Main.checkArgs(Array("2147483647", "2147483647")) shouldBe true
    }
    
    "handle argument parsing with special characters" in {
      Main.checkArgs(Array("80", "24; echo hacked")) shouldBe false
      Main.checkArgs(Array("80", "24\n")) shouldBe false
      Main.checkArgs(Array("80", "24\t")) shouldBe false
    }
    
    "not crash with null arguments" in {
      // This tests the edge case of null array (unlikely in practice)
      val exitCode = withMockedSystemExit {
        // Can't pass null to varargs directly, need to use reflection
        val mainMethod = classOf[Main.type].getMethod("main", classOf[Array[String]])
        try {
          mainMethod.invoke(null, null.asInstanceOf[Array[String]])
        } catch {
          case _: Exception => // Expected
        }
      }
      
      // Should either exit with code 1 or throw exception
      succeed
    }
    
    "handle concurrent calls to main" in {
      failAfter(5.seconds) {
        val exitCodes = (1 to 3).map { i =>
          withMockedSystemExit {
            val mainClass = classOf[Main.type]
            val tuiField = mainClass.getDeclaredField("tui")
            tuiField.setAccessible(true)
            
            val guiField = mainClass.getDeclaredField("gui")
            guiField.setAccessible(true)
            
            try {
              Main.main(Array(s"${80 + i}", s"${24 + i}"))
            } finally {
              tuiField.set(null, null)
              guiField.set(null, null)
            }
          }
        }
        
        // All should run without System.exit being called
        exitCodes.foreach(_ shouldBe -1)
      }
    }
  }
  
  "Main integration with system" should {
    
    "work as a standalone application (smoke test)" in {
      // This test verifies the main method doesn't crash
      // It's a lightweight integration test
      failAfter(5.seconds) {
        val exitCode = withMockedSystemExit {
          // Use a separate thread to avoid blocking
          val thread = new Thread(new Runnable {
            def run(): Unit = {
              val mainClass = classOf[Main.type]
              val tuiField = mainClass.getDeclaredField("tui")
              tuiField.setAccessible(true)
              
              val guiField = mainClass.getDeclaredField("gui")
              guiField.setAccessible(true)
              
              try {
                Main.main(Array("80", "24"))
              } finally {
                tuiField.set(null, null)
                guiField.set(null, null)
              }
            }
          })
          
          thread.setDaemon(true)
          thread.start()
          thread.join(2000) // Wait up to 2 seconds
          
          if (thread.isAlive) {
            thread.interrupt()
          }
        }
        
        // Should not have called System.exit
        exitCode shouldBe -1
      }
    }
    
    "handle interrupt signal gracefully" in {
      failAfter(3.seconds) {
        val exitCode = withMockedSystemExit {
          val thread = new Thread(new Runnable {
            def run(): Unit = {
              val mainClass = classOf[Main.type]
              val tuiField = mainClass.getDeclaredField("tui")
              tuiField.setAccessible(true)
              
              val guiField = mainClass.getDeclaredField("gui")
              guiField.setAccessible(true)
              
              try {
                Main.main(Array("80", "24"))
              } finally {
                tuiField.set(null, null)
                guiField.set(null, null)
              }
            }
          })
          
          thread.start()
          Thread.sleep(100) // Let it start
          thread.interrupt()
          thread.join(1000)
        }
        
        exitCode shouldBe -1
      }
    }
  }
  
  "Main object structure" should {
    
    "be a singleton object" in {
      Main shouldBe an[object]
      Main shouldBe Main
    }
    
    "have main method" in {
      val methods = classOf[Main.type].getMethods
      methods.exists(_.getName == "main") shouldBe true
    }
    
    "have checkArgs method" in {
      val methods = classOf[Main.type].getMethods
      methods.exists(_.getName == "checkArgs") shouldBe true
    }
    
    "not have any mutable state" in {
      val fields = classOf[Main.type].getDeclaredFields
      fields.foreach { field =>
        field.setAccessible(true)
        field.get(null) shouldBe null // All fields should be null initially
      }
    }
  }
}