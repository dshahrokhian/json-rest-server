package sneakPeak

import org.json4s._
import org.json4s.native.JsonMethods._

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
}