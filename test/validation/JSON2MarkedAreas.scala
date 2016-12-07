package validation

import org.json4s.DefaultFormats
import org.json4s.jvalue2extractable
import org.json4s.native.JsonMethods.parse
import org.json4s.string2JsonInput

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