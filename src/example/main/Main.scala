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
  
  def getResultImage(image: BufferedImage, heatMap: BufferedImage, 
      markedMap: BufferedImage): BufferedImage = {
    
    val combined = Jaccard.visualization(heatMap, markedMap)
    
    val graphics = image.getGraphics()
    graphics.drawImage(combined, 0, 0, null)
    
    return image
  }
  
  def main(args: Array[String]) {
    
    val imagePath = "src/example/resources/";
    val dataPath = imagePath + "userData/test/"
    val validationPath = imagePath + "userData/validation/"
    
    val image = ImageIO.read(new File(imagePath + "test_image.jpg"))
    val algorithm = new SneakPeekAlgorithm(image)
    val validation = new MarkedAreasVisualization(image)
    
    // Read all event data
    val nEvents = Option(new File(dataPath).list).map(_.filter(_.endsWith(".json")).size).getOrElse(0)
    for (i <- 0 until nEvents) {
      val ev = JSON2InterestEvent.file2interestEvent(dataPath + i + ".json")
      algorithm.addEvent(ev)
    }
    
    val markedAreas = JSON2MarkedAreas.file2markedAreas(validationPath + "val.json")
    validation.setAreas(markedAreas)
    
    val heatMap = algorithm.getHeatMap()
    val markedMap = validation.getMarkedAreas()
    
    // Save as new image
    ImageIO.write(heatMap, "PNG", new File(imagePath, "heatmap_image.png"));
    ImageIO.write(markedMap, "PNG", new File(imagePath, "marked_image.png"));

    val combined = getResultImage(image, heatMap, markedMap)
    
    // Analysis results
    println("Jaccard similarity: " + Jaccard.similarity(markedMap, heatMap))
    ImageIO.write(combined, "PNG", new File(imagePath, "validation.png"));
  }
}