package sneakPeek

case class InterestEvent(upper_left_x: Int,
    upper_left_y: Int,
    bottom_right_x: Int,
    bottom_right_y: Int,
    time: Long,
    is_final: Boolean,
    op_order: Int)