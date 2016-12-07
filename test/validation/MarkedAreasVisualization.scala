package validation

import java.awt.Color
import java.awt.image.BufferedImage

class MarkedAreasVisualization {
  
  private var image : BufferedImage = _
  private var heatMap = Array(Array[Int]())
  
  def this(img: BufferedImage) = {
    this()
    image = img
    heatMap = Array.ofDim[Int](image.getWidth, image.getHeight)
  }
  
  def setAreas(markedAreas: MarkedAreas) : Unit = {
    for(
        area <- markedAreas.areas;
		    x <- area.x until (area.x + area.width);
			 	y <- area.y until (area.y + area.height)
			  ) {
		  heatMap(x)(y) = 0xc000ff00 // Green with 50% opacity
		}
  }
  
  def getMarkedAreasOverlay() : BufferedImage = {
    val overlay = getMarkedAreas()
    
    val combined = new BufferedImage(image.getWidth, image.getHeight, 
        BufferedImage.TYPE_INT_ARGB)
    
    val graphics = combined.getGraphics();
    graphics.drawImage(image, 0, 0, null);
    graphics.drawImage(overlay, 0, 0, null);
    
    return combined
  }
  
  def getMarkedAreas() : BufferedImage = {
    val output = new BufferedImage(image.getWidth, image.getHeight, 
        BufferedImage.TYPE_INT_ARGB)
    
    val graphics = output.createGraphics()
    graphics.setPaint ( new Color ( 0, 0, 0, 0 ) )
    graphics.fillRect ( 0, 0, image.getWidth, image.getHeight )
    
    for (x <- 0 until image.getWidth) {
      for (y <- 0 until image.getHeight) {
        output.setRGB(x, y, heatMap(x)(y)) 
      }
    }
    
    return output
  } 
}