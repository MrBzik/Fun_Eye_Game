package com.example.eyetoeye

object Utils {

    fun getRightEyeStatePic(value : Int) : Int{
        return when(value){
            STATE_CALM_LOOK -> R.drawable.right_eye_calm
            STATE_SIDE_LOOK -> R.drawable.right_eye_sidelook_default
            STATE_SIDE_LOOK_TEAR -> R.drawable.right_eye_sidelook_red_tear
            STATE_CLOSED_EYE -> R.drawable.right_eye_closed
            STATE_ANGRY -> R.drawable.right_eye_angry
            STATE_ANGRY_1 -> R.drawable.right_eye_angry_1
            STATE_ANGRY_2 -> R.drawable.right_eye_angry_2
            STATE_ANGRY_3 -> R.drawable.right_eye_angry_3
            STATE_ANGRY_4 -> R.drawable.right_eye_angry_4
            STATE_EYE_EXPLODE_1 -> R.drawable.right_eye_explode_start
            STATE_EYE_EXPLODE_2 -> R.drawable.right_eye_explode_middle
            STATE_EYE_EXPLODE_3 -> R.drawable.right_eye_explode_end
            STATE_EYE_DONE -> R.drawable.right_eye_done
            STATE_SIDE_LOOK_DONE -> R.drawable.right_eye_sidelook_done
            STATE_CLOSED_EYE_DONE -> R.drawable.right_eye_closed_done
            else -> R.drawable.right_eye_calm
        }
    }


    fun getLeftEyeStatePic(value : Int) : Int{
        return when(value){
            STATE_CALM_LOOK -> R.drawable.left_eye_calm
            STATE_SIDE_LOOK -> R.drawable.left_eye_sidelook_default
            STATE_SIDE_LOOK_TEAR -> R.drawable.left_eye_sidelook_red_tear
            STATE_CLOSED_EYE -> R.drawable.left_eye_closed
            STATE_ANGRY -> R.drawable.left_eye_angry
            STATE_ANGRY_1 -> R.drawable.left_eye_angry_1
            STATE_ANGRY_2 -> R.drawable.left_eye_angry_2
            STATE_ANGRY_3 -> R.drawable.left_eye_angry_3
            STATE_ANGRY_4 -> R.drawable.left_eye_angry_4
            STATE_EYE_EXPLODE_1 -> R.drawable.left_eye_explode_start
            STATE_EYE_EXPLODE_2 -> R.drawable.left_eye_explode_middle
            STATE_EYE_EXPLODE_3 -> R.drawable.left_eye_explode_end
            STATE_EYE_DONE -> R.drawable.left_eye_done
            STATE_SIDE_LOOK_DONE -> R.drawable.left_eye_sidelook_done
            STATE_CLOSED_EYE_DONE -> R.drawable.left_eye_closed_done
            else -> R.drawable.left_eye_calm
        }
    }

}