package de.htwg.se.util

import scala.xml._
import scala.io.Source
import play.api.libs.json._
import java.io.{File, PrintWriter}
import de.htwg.se.config.Implicits.given
import scala.util.{Try, Success, Failure}

trait FileIO:
    def save(filename: String, sitename: String, content: String): Boolean
    def load(filename: String): List[String]

class JsonFormat extends FileIO {
    def save(filename: String, sitename: String, content: String): Boolean = {
        val listToSave: List[String] = List(sitename, content)
        val json = Json.prettyPrint(Json.toJson(listToSave))
        val result = Try {
            val writer = new PrintWriter(new File(filename))
            writer.write(json)
            writer.close()
        }
        result match {
            case Success(_) => true
            case Failure(_) => false
        }
    }

    def load(filename: String): List[String] = {
        val source = Source.fromFile(filename)
        val jsonString = try source.mkString finally source.close()
        val jsValue = Json.parse(jsonString)

        jsValue.validate[List[String]] match {
            case JsSuccess(value, _) => value
            case JsError(_) => Nil
        }
    }
}

class XMLFormat extends FileIO {
    def save(filename: String, sitename: String, content: String): Boolean = {
        val xml =
        <Website>
            <Name> {sitename} </Name>
            <Content> {content} </Content>
        </Website>

        val result = Try(XML.save(filename, xml, "UTF-8", xmlDecl = true))
        result match
        case scala.util.Success(_) => true
        case scala.util.Failure(ex) => false
    }

    def load(filename: String): List [String] = {
        val nodeXML = XML.loadFile(filename)
        val sitename: String = (nodeXML \ "Name").text
        val content: List[String] =(nodeXML \ "Content").map(_.text).toList
        sitename :: "\n" :: content
    }
}