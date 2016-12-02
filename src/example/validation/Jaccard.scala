package example.validation

import java.awt.image.BufferedImage
import scala.util.Try

object Jaccard {
  private val ALPHA = 24; /* Shifting with this mask will only change the
  																Alpha byte in a ARGB Int */
  
  def similarity(img1: BufferedImage, img2: BufferedImage) : Float = {
    
    var union = 0
    var intersection = 0F
    
    for (
        x <- 0 until img1.getWidth;
        y <- 0 until img1.getHeight
        ) {
      if (
          (img1.getRGB(x, y) >> ALPHA) != 0
          && (img2.getRGB(x, y) >> ALPHA) != 0
          ) {
        intersection += 1
      }
      
      if (
          (img1.getRGB(x, y) >> ALPHA) != 0
          || (img2.getRGB(x, y) >> ALPHA) != 0
          ) {
        union += 1
      }
    }
    
    return Try(intersection/union).getOrElse(0)
  }
}