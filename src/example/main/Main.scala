package example.main

import sneakPeak.SneakPeakAlgorithm
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File
import sneakPeak.JSON2InterestEvent
import sneakPeak.JSON2InterestEvent

object Main {
  def main(args: Array[String]) {
    
    val imagePath = "src/example/resources/";
    val dataPath = "src/example/resources/userData/"
    
    val image = ImageIO.read(new File(imagePath + "image.jpg"))
    val algo = new SneakPeakAlgorithm(image)
    
    for (i <- 0 to 18) {
      val ev = JSON2InterestEvent.file2interestEvent(dataPath + i + ".json")
      algo.addEvent(ev)
    }
    
    val heatMap = algo.getHeatMapOverlay()
    
    // Save as new image
    ImageIO.write(heatMap, "PNG", new File(imagePath, "result.png"));
  }
}