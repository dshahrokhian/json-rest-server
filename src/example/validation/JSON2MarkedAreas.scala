package example.validation

import org.json4s._
import org.json4s.native.JsonMethods._

object JSON2MarkedAreas {
  implicit val formats = DefaultFormats
  
  def string2markedAreas(json: String) : MarkedAreas = {
    return parse(json).extract[MarkedAreas]
  }
  
  def file2markedAreas(fileName: String) : MarkedAreas = {
    var json: String = ""
    
    for (line <- io.Source.fromFile(fileName).getLines) json += line
    
    return string2markedAreas(json);
  }
}