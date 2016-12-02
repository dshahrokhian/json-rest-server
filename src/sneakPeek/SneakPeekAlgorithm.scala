package sneakPeek

import Math.abs
import Math.max
import Math.min
import java.awt.image.BufferedImage
import java.awt.Graphics2D
import java.awt.Color
import java.awt.Graphics

class SneakPeekAlgorithm {  
  
  private val RED = 16; /* Shifting with this mask will only change the
                                   Red byte in a ARGB Int */
  private val ALPHA = 24; /* Shifting with this mask will only change the
  																Alpha byte in a ARGB Int */
  private var image : BufferedImage = _
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
      apply(getInterest(ev), prevEvent.get)
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
    
    for (x <- 0 until image.getWidth) {
      for (y <- 0 until image.getHeight) {
        // Create a Red overlay with the interest, with a maximum transparency of 50%
        if(heatMap(x)(y) > 0) {
          output.setRGB(x, y, 
            (Math.round(normalize(heatMap(x)(y))*0.5F) << ALPHA)
            + (255 << RED) )
        } else {
          output.setRGB(x, y, (0 << ALPHA))
        }
      }
    }
    
    return output;
  }
  
  /** Given a new event, determines how meaningful was the previous one. 
   *  In particular, the difference in time-stamps from the previous event to 
   *  this new one determines how much time the user spent in the previous area.
   *  
   *  The interest depends on both time spent and area. Smaller the area,
   *  greater the interest weight applied to it. It is done in such a way that
   *  if the area of the previous event is equal to the whole image, the 
   *  interest is zero, no matter how much time the user spent on it. The 
   *  reason for this decision is that we want to gather the interest areas 
   *  on the image, and the user looking at the whole image itself does not give us 
   *  any useful insight.
   * 
   * @param ev new interest-event needed to calculate the impact of the previous
   * area
   * @return interest of the previous recorded event
   */
  private def getInterest(ev : InterestEvent) : Float = {
    
    val areaWidth = abs(prevEvent.get.upper_left_x - prevEvent.get.bottom_right_x)
    val areaHeight = abs(prevEvent.get.upper_left_y - prevEvent.get.bottom_right_y)
    
    val timeDiff = ev.time - prevEvent.getOrElse(ev).time
    val areaDiff = abs(image.getWidth - areaWidth) 
                    + abs(image.getHeight - areaHeight)
                    
    return areaDiff / 2 * timeDiff
  }
  
  /** Applies the interest to the event's area, and updates the maximum interest seen so far */
  private def apply(interest : Float, ev : InterestEvent) = {
    for (x <- ev.upper_left_x until ev.bottom_right_x) {
      for (y <- ev.upper_left_y until ev.bottom_right_y) {
        heatMap(x)(y) += interest
        maxInterestVal = max(maxInterestVal, heatMap(x)(y))
      }
    }
  }
  
  /** Normalizes the interest value to a Byte range */
  private def normalize(value: Float) : Int = {
    return Math.round((value) / maxInterestVal * 255)
  }

}