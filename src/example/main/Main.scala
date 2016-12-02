package example.main

import java.io.File

import example.validation.MarkedAreasVisualization
import javax.imageio.ImageIO
import sneakPeek.JSON2InterestEvent
import sneakPeek.SneakPeekAlgorithm
import example.validation.JSON2MarkedAreas
import java.awt.image.BufferedImage
import example.validation.Jaccard

object Main {
  def main(args: Array[String]) {
    
    val imagePath = "src/example/resources/";
    val dataPath = imagePath + "userData/test/"
    val validationPath = imagePath + "userData/validation/"
    
    val image = ImageIO.read(new File(imagePath + "test_image.jpg"))
    
    val algorithm = new SneakPeekAlgorithm(image)
    val validation = new MarkedAreasVisualization(image)
    
    for (i <- 0 to 18) {
      val ev = JSON2InterestEvent.file2interestEvent(dataPath + i + ".json")
      algorithm.addEvent(ev)
    }
    
    val markedAreas = JSON2MarkedAreas.file2markedAreas(validationPath + "val.json")
    validation.setAreas(markedAreas)
    
    val heatMap = algorithm.getHeatMap()
    val markedMap = validation.getMarkedAreas()
    
    // Save as new image
    ImageIO.write(heatMap, "PNG", new File(imagePath, "result_image.png"));
    ImageIO.write(markedMap, "PNG", new File(imagePath, "marked_image.png"));

    val combined = new BufferedImage(image.getWidth, image.getHeight, 
        BufferedImage.TYPE_INT_ARGB)
    
    val graphics = combined.getGraphics();
    graphics.drawImage(image, 0, 0, null);
    graphics.drawImage(markedMap, 0, 0, null);
    graphics.drawImage(heatMap, 0, 0, null);
    
    println("Jaccard similarity: " + Jaccard.similarity(markedMap, heatMap))
    ImageIO.write(combined, "PNG", new File(imagePath, "validation.png"));
  }
}