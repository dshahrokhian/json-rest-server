package example.main

import java.io.File

import example.validation.MarkedAreasVisualization
import javax.imageio.ImageIO
import sneakPeek.JSON2InterestEvent
import sneakPeek.SneakPeekAlgorithm
import example.validation.JSON2MarkedAreas

object Main {
  def main(args: Array[String]) {
    
    val imagePath = "src/example/resources/";
    val dataPath = imagePath + "userData/test/"
    val validationPath = imagePath + "userData/validation/"
    
    val image = ImageIO.read(new File(imagePath + "test_image.jpg"))
    val algo = new SneakPeekAlgorithm(image)
    val validation = new MarkedAreasVisualization(image)
    
    for (i <- 0 to 18) {
      val ev = JSON2InterestEvent.file2interestEvent(dataPath + i + ".json")
      algo.addEvent(ev)
    }
    val heatMap = algo.getHeatMapOverlay()
    
    val markedAreas = JSON2MarkedAreas.file2markedAreas(validationPath + "val.json")
    validation.setAreas(markedAreas)
    val markedMap = validation.getMarkedAreasOverlay()
    
    // Save as new image
    ImageIO.write(heatMap, "PNG", new File(imagePath, "result_image.png"));
    ImageIO.write(markedMap, "PNG", new File(imagePath, "marked_image.png"));
  }
}