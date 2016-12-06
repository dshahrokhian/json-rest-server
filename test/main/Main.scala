package main

import java.io.File

import validation.MarkedAreasVisualization
import javax.imageio.ImageIO
import sneakPeek.JSON2InterestEvent
import sneakPeek.SneakPeekAlgorithm
import validation.JSON2MarkedAreas
import java.awt.image.BufferedImage
import validation.Jaccard
import validation.MarkedAreas
import scala.reflect.io.Path
import scala.util.Try

object Main {
  
  def getResultImage(image: BufferedImage, heatMap: BufferedImage, 
      markedMap: BufferedImage): BufferedImage = {
    val result = new BufferedImage(image.getWidth, image.getHeight, 
        BufferedImage.TYPE_INT_ARGB)
    
    val combined = Jaccard.visualization(heatMap, markedMap)
    
    val graphics = result.getGraphics()
    graphics.drawImage(image, 0, 0, null)
    graphics.drawImage(combined, 0, 0, null)
    
    return result
  }
  
  def getUserMarkedAreas(test: String, user: String, allMarkedAreas: Array[MarkedAreas]): MarkedAreas = {
    for (userMarkedAreas <- allMarkedAreas) {
      if (userMarkedAreas.user == user && userMarkedAreas.test == test) {
        return userMarkedAreas
      }
    }
    
    throw new NoSuchElementException("The [test,user]= [" + user + "," + test +
        "] was not found in the validation set.")
  }
  
  def getListOfSubDirectories(directoryName: String): Array[String] = {
    return (new File(directoryName)).listFiles.filter(_.isDirectory).map(_.getName)
  }
  
  def main(args: Array[String]) {
    
    val dataPath = "test/resources/user_data/test/"
    val validationPath = "test/resources/user_data/validation/"
    
    val allMarkedAreas = JSON2MarkedAreas.file2markedAreas(validationPath + "all.json")
    
    val tests = getListOfSubDirectories(dataPath)
    
    for (test <- tests) {
      val testPath = dataPath + test + "/";

      val image = ImageIO.read(new File(testPath + "test_image.jpg"))
    
      for (user <- getListOfSubDirectories(testPath)) {
        val userRecordingPath = testPath + user + "/"
        
        val algorithm = new SneakPeekAlgorithm(image)
        val validation = new MarkedAreasVisualization(image)
        
        // Read all event data
        val nEvents = Option(
                            new File(userRecordingPath).list)
                            .map(_.filter(_.endsWith(".json")).size
                            ).getOrElse(0)
                            
        for (i <- 0 until nEvents) {
          val ev = JSON2InterestEvent.file2interestEvent(userRecordingPath + i + ".json")
          algorithm.addEvent(ev)
        }
        val heatMap = algorithm.getHeatMap()
        
        // Validate the results of the algorithm
        validation.setAreas(getUserMarkedAreas(test, user, allMarkedAreas))
        val markedMap = validation.getMarkedAreas()
        println("Jaccard similarity[" + "test=" + test + ", user=" + user + "]: " 
            + Jaccard.similarity(markedMap, heatMap))
        
        // Save as new image
        ImageIO.write(heatMap, "PNG", new File(userRecordingPath, "heatmap_image.png"));
        ImageIO.write(markedMap, "PNG", new File(userRecordingPath, "marked_image.png"));
        val combined = getResultImage(image, heatMap, markedMap)
        ImageIO.write(combined, "PNG", new File(userRecordingPath, "validation.png"));
      }
    }
  }
}