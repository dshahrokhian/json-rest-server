package main

import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import sneakPeek.JSON2InterestEvent
import sneakPeek.Masks.ALPHA
import sneakPeek.SneakPeekAlgorithm
import validation.JSON2MarkedAreas
import validation.Jaccard
import validation.MarkedAreas
import validation.MarkedAreasVisualization

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
  
  def getAverageHeatMap(heatMaps: Array[BufferedImage]): BufferedImage = {
    val width = heatMaps(0).getWidth
    val height = heatMaps(0).getHeight
    
    val result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val avgPixels = Array.tabulate(width, height)( (x,y) => 0 )
    
    for (
        heatMap <- heatMaps;
        x <- 0 until width;
        y <- 0 until height
        ) {
      // Extract Alpha Channel
      avgPixels(x)(y) = avgPixels(x)(y) + ( (heatMap.getRGB(x, y) & 0xff000000) >>> ALPHA) 
    }
    
    for (
        x <- 0 until width;
        y <- 0 until height
        ) {
      avgPixels(x)(y) = ( ((avgPixels(x)(y) / heatMaps.length) << ALPHA) | 0x00ff0000 )
      // Filter only results above the threshold (25% opacity)
      if ((avgPixels(x)(y) >>> ALPHA) > 64) {
        result.setRGB(x, y, avgPixels(x)(y))
      }
    }
    
    return result
  }

  
  def main(args: Array[String]) {
    
    val dataPath = "test/resources/user_data/test/"
    val validationPath = "test/resources/user_data/validation/"
    
    val allMarkedAreas = JSON2MarkedAreas.file2markedAreas(validationPath + "all.json")
    
    val tests = getListOfSubDirectories(dataPath)
    
    for (test <- tests) {
      var mean = 0F
      var max = 0F
      var min = Float.MaxValue
      val testPath = dataPath + test + "/";
      
      val heatMaps = new Array[BufferedImage](getListOfSubDirectories(testPath).length)
      
      val image = ImageIO.read(new File(testPath + "test_image.jpg"))

      val userRecords = getListOfSubDirectories(testPath)
      for (i <- 0 until userRecords.length) {
        val user = userRecords(i)
        val userRecordingPath = testPath + user + "/"
        
        val algorithm = new SneakPeekAlgorithm(image)
        val validation = new MarkedAreasVisualization(image)
        
        // Read all event data
        val events = JSON2InterestEvent.folder2interestEvents(userRecordingPath)
        for (ev <- events) {
          algorithm.addEvent(ev)
        }
        val heatMap = algorithm.getHeatMap()
        heatMaps(i) = heatMap
        
        // Validate the results of the algorithm
        validation.setAreas(getUserMarkedAreas(test, user, allMarkedAreas))
        val markedMap = validation.getMarkedAreas()
        
        mean += Jaccard.similarity(markedMap, heatMap)
        max = Math.max(max, Jaccard.similarity(markedMap, heatMap))
        min = Math.min(min, Jaccard.similarity(markedMap, heatMap))
        // Save as new image
        ImageIO.write(heatMap, "PNG", new File(userRecordingPath, "heatmap_image.png"));
        ImageIO.write(markedMap, "PNG", new File(userRecordingPath, "marked_image.png"));
        val combined = getResultImage(image, heatMap, markedMap)
        ImageIO.write(combined, "PNG", new File(userRecordingPath, "validation_image.png"));
      }
      val avgHeatMap = getAverageHeatMap(heatMaps)
      ImageIO.write(avgHeatMap, "PNG", new File(testPath, "average_image.png"));
      println("mean jaccard for image " + test + ": " + (mean/userRecords.length))
      println("max jaccard for image " + test + ": " + (max))
      println("min jaccard for image " + test + ": " + (min))
    }
  }
}
