package sneakPeak

import org.json4s._
import org.json4s.native.JsonMethods._

class JSON2InterestEvent {
  implicit val formats = DefaultFormats
  
  final def string2interestEvent(json: String) : InterestEvent = {
    return parse(json).extract[InterestEvent]
  }
  
  final def file2interestEvent(fileName: String) : InterestEvent = {
    var json: String = ""
    
    for (line <- io.Source.fromFile(fileName).getLines) json += line
    
    return string2interestEvent(json);
  }
}