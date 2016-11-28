package sneakPeak

import Math.abs
import Math.max
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
    val interest = getInterest(ev)
    
    if (prevEvent.isDefined) {
      apply(interest, prevEvent.get)
    }
    
    this.prevEvent = Some(ev);
    this.maxInterestVal = max(this.maxInterestVal, interest)
    this.minInterestVal = max(this.minInterestVal, interest)
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
    
    val graphics = output.createGraphics()
    graphics.setPaint ( new Color ( 0, 0, 0, 0 ) )
    graphics.fillRect ( 0, 0, image.getWidth, image.getHeight )
    
    for (x <- 0 to image.getWidth) {
      for (y <- 0 to image.getHeight) {
        // Create a Red overlay with the interest, with a transparency of 75%
        output.setRGB(x, y, 
            (normalize(heatMap(x)(y)) << RED)
            + (192 << ALPHA) ) 
      }
    }
    
    return output;
  }
  
  private def getInterest(ev : InterestEvent) : Float = {
    
    val areaWidth = abs(ev.upperLeftX - ev.bottomRightX)
    val areaHeight = abs(ev.upperLeftY - ev.bottomRightY)
    
    val timeDiff = ev.timestamp - prevEvent.getOrElse(ev).timestamp
    val areaDiff = abs(image.getWidth - areaWidth) 
                    + abs(image.getHeight - areaHeight)
    
    return areaDiff / 2 * timeDiff
  }
  
  private def apply(interest : Float, ev : InterestEvent ) = {
    
    for (x <- ev.upperLeftX to ev.bottomRightX) {
      for (y <- ev.upperLeftY to ev.bottomRightY) {
        heatMap(x)(y) += interest
      }
    }
  }
  
  private def normalize(value : Float) : Int = {
    return Math.round((value - this.minInterestVal) 
            / this.maxInterestVal * 255)
  }
}