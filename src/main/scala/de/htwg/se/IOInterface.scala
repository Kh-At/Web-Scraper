package de.htwg.webcrawler {
  import scala.io.StdIn.readLine
  import java.nio.file.{Files, Paths, StandardOpenOption}
  import scala.util.{Using, Try, Success, Failure}

  object IOInterface {

    // Reads the user input and puts it into a file.
    def ioToFile(outputFile: String): Unit  = {
      val linesToWrite = readLine()
      Using( Files.newBufferedWriter (Paths.get(outputFile), StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING)) {
        writer => writer.write(linesToWrite + "\n")
      }
    }
  }
}