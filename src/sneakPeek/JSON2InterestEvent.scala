package sneakPeek

import java.io.File

import org.json4s.DefaultFormats
import org.json4s.jvalue2extractable
import org.json4s.native.JsonMethods.parse
import org.json4s.string2JsonInput

object JSON2InterestEvent {
  implicit val formats = DefaultFormats
  
  def string2interestEvent(json: String) : InterestEvent = {
    return parse(json).extract[InterestEvent]
  }
  
  def file2interestEvent(fileName: String) : InterestEvent = {
    var json: String = ""
    
    for (line <- io.Source.fromFile(fileName).getLines) json += line
    
    return string2interestEvent(json);
  }
  
  def folder2interestEvents(path: String) : Array[InterestEvent] = {
    
    val nEvents = Option(
                        new File(path).list)
                        .map(_.filter(_.endsWith(".json")).size
                        ).getOrElse(0)

    val interestEvents = new Array[InterestEvent](nEvents)

    for (i <- 0 until nEvents) {
      val ev = JSON2InterestEvent.file2interestEvent(path + i + ".json")
      interestEvents(i) = ev
    }
    
    return interestEvents
  }
}