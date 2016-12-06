package validation

import java.awt.image.BufferedImage
import scala.util.Try

object Jaccard {
  private val ALPHA = 24; /* Shifting with this mask will only change the
  																Alpha byte in a ARGB Int */
  
  def similarity(img1: BufferedImage, img2: BufferedImage) : Float = {
    assertSameSize(img1, img2)
    
    var union = 0
    var intersection = 0F
    
    for (
        x <- 0 until img1.getWidth;
        y <- 0 until img1.getHeight
        ) {
      if (
          (img1.getRGB(x, y) >>> ALPHA) >= 64
          && (img2.getRGB(x, y) >>> ALPHA) >= 64
          ) {
        intersection += 1
      }
      
      if (
          (img1.getRGB(x, y) >>> ALPHA) >= 64
          || (img2.getRGB(x, y) >>> ALPHA) >= 64
          ) {
        union += 1
      }
    }
    return Try(intersection/union).getOrElse(0)
  }
  
  def visualization(heatMap: BufferedImage, markedAreas: BufferedImage) : BufferedImage = {
    assertSameSize(heatMap, markedAreas)
    
    val combined = new BufferedImage(heatMap.getWidth, markedAreas.getHeight, 
        BufferedImage.TYPE_INT_ARGB)
    
    for (
        x <- 0 until combined.getWidth;
        y <- 0 until combined.getHeight
        ) {
      if (
          (heatMap.getRGB(x, y) >>> ALPHA) >= 64
          && (markedAreas.getRGB(x, y) >>> ALPHA) >= 64
          ) {
        combined.setRGB(x, y, 0xc0ffff00) // Set pixel to Yellow
      } else {
        if ((heatMap.getRGB(x, y) >>> ALPHA) >= 64) {
          combined.setRGB(x, y, heatMap.getRGB(x, y))
        } else if ((markedAreas.getRGB(x, y) >>> ALPHA) >= 64) {
          combined.setRGB(x, y, markedAreas.getRGB(x, y))
        }
      }  
    }
    
    return combined
  }
  
  private def assertSameSize(img1: BufferedImage, img2: BufferedImage): Unit = {
    assert(img1.getWidth == img2.getWidth && img1.getHeight == img1.getHeight
        , "Images are not of the same size.")
  }
}