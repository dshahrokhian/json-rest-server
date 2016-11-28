package sneakPeak

import Math.abs
import Math.max
import Math.min
import java.awt.image.BufferedImage
import java.awt.Graphics2D
import java.awt.Color
import java.awt.Graphics

class SneakPeakAlgorithm {  
  
  private val RED = 16; /* Shifting with this mask will only change the
                                   Red byte in a ARGB Int */
  private val ALPHA = 24; /* Shifting with this mask will only change the
  																Alpha byte in a ARGB Int */
  private var image : BufferedImage = _
  private var minInterestVal = Float.MaxValue
  private var maxInterestVal = Float.MinValue
  private var prevEvent: Option[InterestEvent] = None
  private var heatMap = Array(Array[Float]())
  
  def this(img: BufferedImage) = {
    
    this()
    image = img
    heatMap = Array.ofDim[Float](image.getWidth, image.getHeight)
  }
  
  /** Adds a new event to the algorithm. This new event will modify the 
   *  interest measurement of the previous event.
   *  
   *  @param ev Event to be added to the algorithm
   */
  def addEvent(ev: InterestEvent) : Unit = {
    
    if (prevEvent.isDefined) {
      val interest = getInterest(ev)
      
      apply(interest, prevEvent.get)
      
      this.maxInterestVal = max(this.maxInterestVal, interest)
      if (interest != Float.MinValue) {
        this.minInterestVal = min(this.minInterestVal, interest)
      }
    }
    
    prevEvent = Some(ev);
  }
  
  /** Generates a heat-map of the most visited zones of the image, overlaid
   *  of the original image */
  def getHeatMapOverlay() : BufferedImage = {
    val overlay = getHeatMap()
    
    val combined = new BufferedImage(image.getWidth, image.getHeight, 
        BufferedImage.TYPE_INT_ARGB)
    
    val graphics = combined.getGraphics();
    graphics.drawImage(image, 0, 0, null);
    graphics.drawImage(overlay, 0, 0, null);
    
    return combined
  }
  
  /** Generates a heat-map of the most visited zones of the image. */
  def getHeatMap() : BufferedImage = {
    val output = new BufferedImage(image.getWidth, image.getHeight, 
        BufferedImage.TYPE_INT_ARGB)
    
    // Set the heat-map overlay to be completely transparent at the beginning
    //val graphics = output.createGraphics()
    //graphics.setPaint ( new Color ( 0, 0, 0, 255 ) )
    //graphics.fillRect ( 0, 0, image.getWidth, image.getHeight )
    
    for (x <- 0 until image.getWidth) {
      for (y <- 0 until image.getHeight) {
        // Create a Red overlay with the interest, with a transparency of 75%
        output.setRGB(x, y, 
            (normalize(heatMap(x)(y)) << RED)
            + (192 << ALPHA) ) 
      }
    }
    
    return output;
  }
  
  private def getInterest(ev : InterestEvent) : Float = {
    
    val areaWidth = abs(prevEvent.get.upper_left_x - prevEvent.get.bottom_right_x)
    val areaHeight = abs(prevEvent.get.upper_left_y - prevEvent.get.bottom_right_y)
    
    val timeDiff = ev.time - prevEvent.getOrElse(ev).time
    val areaDiff = abs(image.getWidth - areaWidth) 
                    + abs(image.getHeight - areaHeight)
                    
    return areaDiff / 2 * timeDiff
  }
  
  private def apply(interest : Float, ev : InterestEvent) = {
    for (x <- ev.upper_left_x until ev.bottom_right_x) {
      for (y <- ev.upper_left_y until ev.bottom_right_y) {
        heatMap(x)(y) += interest
      }
    }
  }
  
  private def normalize(value : Float) : Int = {
    return Math.round((value - this.minInterestVal) 
            / this.maxInterestVal * 255)
  }
}