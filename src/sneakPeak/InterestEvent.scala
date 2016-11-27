package sneakPeak

case class InterestEvent(upperLeftX: Int,
    upperLeftY: Int,
    bottomRightX: Int,
    bottomRightY: Int,
    timestamp: Long,
    isFinal: Boolean,
    opOrder: Int)