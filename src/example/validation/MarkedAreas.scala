package example.validation

case class Area(x: Int, y: Int, width: Int, height: Int)
case class MarkedAreas(user: String, test: String, areas: Array[Area])