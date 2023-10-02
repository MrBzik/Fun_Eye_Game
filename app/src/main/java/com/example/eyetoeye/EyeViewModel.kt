package com.example.eyetoeye

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


const val STATE_CALM_LOOK = 0
const val STATE_SIDE_LOOK = 1
const val STATE_CLOSED_EYE = 2
const val STATE_SIDE_LOOK_TEAR = 3
const val STATE_ANGRY = 4
const val STATE_ANGRY_1 = 5
const val STATE_ANGRY_2 = 6
const val STATE_ANGRY_3 = 7
const val STATE_ANGRY_4 = 8
const val STATE_EYE_EXPLODE_1 = 9
const val STATE_EYE_EXPLODE_2 = 10
const val STATE_EYE_EXPLODE_3 = 11
const val STATE_EYE_DONE = 12
const val STATE_SIDE_LOOK_DONE = 13
const val STATE_CLOSED_EYE_DONE = 14

const val AUDIO_DEFAULT = 0
const val AUDIO_SAD_TROMBONE = 1
const val AUDIO_WIN = 2
const val AUDIO_PUNCH_1 = 3
const val AUDIO_PUNCH_2 = 4
const val AUDIO_CRY = 5
const val AUDIO_POP = 6



class EyeViewModel : ViewModel() {

    private val _remainingHearts = MutableStateFlow(10)
    val remainingHearts = _remainingHearts.asStateFlow()

    private var isPressing = false

    private var rightEyeDamage = 0
    var isRightEyeDone = false

    private var angerLevel = STATE_ANGRY


    private val _isAWin = MutableStateFlow(false)
    val isAWin = _isAWin.asStateFlow()


    private val _audioQues = MutableStateFlow(AUDIO_DEFAULT)
    val audioQues = _audioQues.asStateFlow()


    private val _rightEyeState = MutableStateFlow(STATE_CALM_LOOK)
    val rightEyeState = _rightEyeState.asStateFlow()

    fun pressEye(isInSync : () -> Boolean){
        releaseJob?.cancel()
        randomActJob?.cancel()
        pressingJob?.cancel()
        isPressing = true



            pressingJob = viewModelScope.launch {
                if(_rightEyeState.value == STATE_SIDE_LOOK ||
                    _rightEyeState.value == STATE_SIDE_LOOK_TEAR ){

                delay(250)
               val areBothButtonsPressed = isInSync()

                if(areBothButtonsPressed){

                    getAngryLook()

                    while (true){
                        delay(400)
                        rightEyeDamage ++
                        getAngryLook()
                    }

                } else {
                    closeEyes()
                }
            } else {
                    closeEyes()
                }
        }


    }


    private var releaseJob : Job? = null

    private var randomActJob : Job? = null

    private var pressingJob : Job? = null

    fun launchRandomAction (){

        randomActJob?.cancel()

       randomActJob = viewModelScope.launch {

           while (true){
               val delay = (2000..6000).random().toLong()

               delay(delay)

               val action = (0..10).random()

               if(action < 4){
                   sideLook()
               } else {
                   blink()
               }
           }
       }
    }

    fun releasePressure() {
        isPressing = false
        pressingJob?.cancel()
        releaseJob?.cancel()
        randomActJob?.cancel()
        releaseJob = viewModelScope.launch{
            delay(1000)
            if(!isPressing)
                if(rightEyeDamage > 5) getAngryLook() else _rightEyeState.value = STATE_CALM_LOOK

        }
    }



    private fun getAngryLook(){
        _rightEyeState.value =
            when(rightEyeDamage){
                in 0..5 -> STATE_ANGRY
                in 6..10 -> STATE_ANGRY_1
                in 11..15 -> STATE_ANGRY_2
                in 16..20 -> STATE_ANGRY_3
                in 21..25 -> STATE_ANGRY_4
                else -> {
                    if(isRightEyeDone)
                        STATE_EYE_DONE
                    else {
                        isRightEyeDone = true
                        explodeRightEye()
                        STATE_EYE_EXPLODE_1
                    }
                }
            }

        levelUpAnger()

    }


    private fun levelUpAnger(){

        if(_rightEyeState.value > angerLevel &&
                !isRightEyeDone
                ){

            angerLevel = _rightEyeState.value

            pressingJob?.cancel()

            viewModelScope.launch {
                delay(100)
                _rightEyeState.value = STATE_CLOSED_EYE
                _audioQues.value = AUDIO_CRY
                delay(400)
                _audioQues.value = AUDIO_DEFAULT
            }
        }

        if(isRightEyeDone)
            angerLevel = STATE_ANGRY

    }


    private suspend fun closeEyes(){
        if(isRightEyeDone)
            _rightEyeState.value = STATE_CLOSED_EYE_DONE
        else {
            _rightEyeState.value = STATE_CLOSED_EYE
            delay(250)
            _remainingHearts.value --
            if(_remainingHearts.value <= 0){
                _audioQues.value = AUDIO_SAD_TROMBONE
            } else {
                when (_audioQues.value) {
                    AUDIO_PUNCH_1 -> _audioQues.value = AUDIO_PUNCH_2
                    AUDIO_PUNCH_2 -> _audioQues.value = AUDIO_PUNCH_1
                    else -> _audioQues.value = AUDIO_PUNCH_1
                }
            }
        }
    }

    private suspend fun blink(){

        if(isRightEyeDone)
        _rightEyeState.value = STATE_CLOSED_EYE_DONE
        else
         _rightEyeState.value = STATE_CLOSED_EYE

        delay(400)
        if(rightEyeDamage > 5) getAngryLook() else _rightEyeState.value = STATE_CALM_LOOK
    }

    private suspend fun sideLook(){

        if(isRightEyeDone)
            _rightEyeState.value = STATE_SIDE_LOOK_DONE

        else if(rightEyeDamage > 10)
            _rightEyeState.value = STATE_SIDE_LOOK_TEAR

        else _rightEyeState.value = STATE_SIDE_LOOK

        val delay = when(angerLevel){
            STATE_ANGRY -> (1000..3000).random().toLong()
            STATE_ANGRY_1 -> (1000..1400).random().toLong()
            STATE_ANGRY_2 -> (800..1000).random().toLong()
            STATE_ANGRY_3 -> (450..800).random().toLong()
            STATE_ANGRY_4 -> (350..450).random().toLong()
            else -> (1000..3000).random().toLong()
        }

        delay(delay)

        if(rightEyeDamage > 10) getAngryLook() else _rightEyeState.value = STATE_CALM_LOOK
    }

    private fun explodeRightEye() = viewModelScope.launch {

        _audioQues.value = AUDIO_POP
        delay(150)
        _rightEyeState.value = STATE_EYE_EXPLODE_2
        delay(150)
        _rightEyeState.value = STATE_EYE_EXPLODE_3
        delay(150)
        _rightEyeState.value = STATE_EYE_DONE

        delay(1000)
        _isAWin.value = true
        _audioQues.value = AUDIO_WIN
    }



    fun restartGame(){
        rightEyeDamage = 0
        angerLevel = STATE_ANGRY
        _remainingHearts.value = 10
        _audioQues.value = AUDIO_DEFAULT
        _rightEyeState.value = STATE_CALM_LOOK
    }


}







