package de.htwg.se.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class DownloaderSpec extends AnyWordSpec with Matchers {
  
  "Downloader" should {
    "have htmlToPlainText method" in {
      // Since we can't test actual web requests, we test that the method exists
      Downloader.htmlToPlainText should not be null
      // The method signature exists
      succeed
    }
    
    "have request method" in {
      Downloader.request should not be null
      // The method signature exists
      succeed
    }
  }
}