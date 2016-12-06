package validation

import org.json4s._
import org.json4s.native.JsonMethods._

object JSON2MarkedAreas {
  implicit val formats = DefaultFormats
  
  def string2markedAreas(json: String) : Array[MarkedAreas] = {
    return parse(json).extract[Array[MarkedAreas]]
  }
  
  def file2markedAreas(fileName: String) : Array[MarkedAreas] = {
    var json: String = ""
    
    for (line <- io.Source.fromFile(fileName).getLines) json += line
    
    return string2markedAreas(json);
  }
}