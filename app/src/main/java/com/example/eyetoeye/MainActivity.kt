package com.example.eyetoeye

import android.content.Context
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eyetoeye.ui.theme.EyeToEyeTheme
import kotlinx.coroutines.delay

const val VIBRATION_DELAY = 250L
const val VIBRATION_DURATION = 200L

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            EyeToEyeTheme {
                // A surface container using the 'background' color from the theme

                val punchSound = MediaPlayer.create(this, R.raw.punch)

                val eyeViewModel by viewModels<EyeViewModel>()

                var isGameStarted by remember{ mutableStateOf(false) }
                var isGameLost by remember{ mutableStateOf(false) }
                val isAWin = eyeViewModel.isAWin.collectAsState()

                val audioQues = eyeViewModel.audioQues.collectAsState()


                LaunchedEffect(key1 = audioQues.value){

                    when(audioQues.value){
                        AUDIO_SAD_TROMBONE -> MediaPlayer.create(this@MainActivity, R.raw.trambone).start()
                        AUDIO_WIN ->  MediaPlayer.create(this@MainActivity, R.raw.laugh).start()
                        AUDIO_PUNCH_1 -> punchSound.start()
                        AUDIO_PUNCH_2 -> punchSound.start()
                        AUDIO_CRY -> MediaPlayer.create(this@MainActivity, R.raw.cry).start()
                        AUDIO_POP -> MediaPlayer.create(this@MainActivity, R.raw.pop).start()

                    }
                }



                if(isGameStarted){
                    StartGame(eyeViewModel = eyeViewModel, {
                        vibrate()
                    }, {
                        isGameLost = true
                    } )


                } else  {
                    LaunchScreen {
                        isGameStarted = true
                    }
                }


                if(isGameLost){

                    DrawLosingScreen(){
                        isGameLost = false
                        eyeViewModel.restartGame()
                    }
                }

                if(isAWin.value){
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "DONE!",
                        fontSize = 35.sp,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }


    private val vibrator by lazy {
        if(Build.VERSION.SDK_INT >= 31){
            (this.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        }
        else if (Build.VERSION.SDK_INT >= 26) {
            (this.getSystemService(VIBRATOR_SERVICE) as Vibrator)
        } else {
            (this.getSystemService(VIBRATOR_SERVICE) as Vibrator)
        }
    }


    private fun vibrate(){

        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(VIBRATION_DURATION)
        }
    }

}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EyeToEyeTheme {



    }
}





@Composable
fun LaunchScreen(title : String = "ГЛЯДЕЛКИ",
                 buttonText : String = "Play",
                 backgroundColor : Color = Color.Transparent,
    startGameClick : () -> Unit){

    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
        , horizontalAlignment = Alignment.CenterHorizontally) {

        Text(text = title,
            Modifier
                .weight(1f)
                .fillMaxWidth()
            , textAlign = TextAlign.Center, fontSize = 80.sp, letterSpacing = 20.sp, fontWeight = FontWeight.Bold
        )

        Button(modifier = Modifier
            .weight(1f)
            .width(200.dp)
            .padding(top = 50.dp, bottom = 50.dp),
            onClick = {
                startGameClick()
            }) {
            Text(text = buttonText, fontSize = 40.sp)
        }
    }
}



@Composable
fun StartGame(eyeViewModel : EyeViewModel, vibrate : () -> Unit, lostGame : ()-> Unit){

    val eyeState = eyeViewModel.rightEyeState.collectAsState()

    val hearts = eyeViewModel.remainingHearts.collectAsState()
    
    val interactionSource = remember{ MutableInteractionSource() }

    val interactionSourceLeft = remember { MutableInteractionSource() }

    val isPressed by interactionSource.collectIsPressedAsState()

    val isLeftPressed by interactionSourceLeft.collectIsPressedAsState()




    LaunchedEffect(key1 = isPressed){

        if(!isPressed){
            eyeViewModel.releasePressure()
            eyeViewModel.launchRandomAction()
        }
        else {
            eyeViewModel.pressEye(){
                Log.d("CHECKTAGS", "is left pressed? : $isLeftPressed")
                isLeftPressed
            }

            while (!eyeViewModel.isRightEyeDone ){
                vibrate()
                delay(VIBRATION_DELAY)

                if(eyeState.value == STATE_CLOSED_EYE)
                    break
            }
        }
    }


    LaunchedEffect(key1 = isLeftPressed){

        if(!isLeftPressed){
            eyeViewModel.releasePressure()
            eyeViewModel.launchRandomAction()
        }
        else {
            eyeViewModel.pressEye(){
                Log.d("CHECKTAGS", "is right pressed? : $isPressed")
                isPressed
            }

            while (!eyeViewModel.isRightEyeDone ){
                vibrate()
                delay(VIBRATION_DELAY)

                if(eyeState.value == STATE_CLOSED_EYE)
                    break
            }
        }
    }
    

    

    Box(
        contentAlignment =Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Row(Modifier.padding(bottom = 40.dp)) {



            Box(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                , contentAlignment = Alignment.Center) {



                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(
                            start = 15.dp,
                            top = if (eyeState.value == STATE_SIDE_LOOK || eyeState.value == STATE_SIDE_LOOK_TEAR)
                                50.dp else 80.dp
                        )
                    ,
                    contentAlignment = Alignment.Center
                ) {

                    Button(
                        onClick = { },
                        interactionSource = interactionSourceLeft,

                        modifier = Modifier
                            .height(90.dp)
                            .width(135.dp)
                            .alpha(0.4f),
                    ) {

                    }
                }


                Image(painter = painterResource(id = Utils.getLeftEyeStatePic(eyeState.value)), contentDescription = "",
                    Modifier
                        .height(250.dp)
                        .width(250.dp)
                        .background(Color.White)
                )

            }


            Box(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                , contentAlignment = Alignment.Center) {

                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(
                            end = 15.dp,
                            top = if (eyeState.value == STATE_SIDE_LOOK || eyeState.value == STATE_SIDE_LOOK_TEAR)
                                60.dp else 90.dp
                        )
                    ,
                    contentAlignment = Alignment.Center
                ) {

                    Button(
                        onClick = { },
                        interactionSource = interactionSource,

                        modifier = Modifier
                            .height(90.dp)
                            .width(135.dp)
                            .alpha(0.4f),
                    ) {

                    }
                }

                Image(painter = painterResource(id = Utils.getRightEyeStatePic(eyeState.value)), contentDescription = "",
                    Modifier
                        .height(250.dp)
                        .width(250.dp)
                        .background(Color.White)
                )

            }
        }
    }


    DrawHearts(heartsCount = hearts.value){
        lostGame()
    }


}


@Composable
fun DrawHearts(heartsCount : Int, lostAllHearts : () -> Unit){

    if(heartsCount <= 0){
        lostAllHearts()
    }
    
    Row() {

        repeat(heartsCount){
            
            Icon(painter = painterResource(id = R.drawable.ic_full_heart), contentDescription = "", tint = Color.Red)
            
        }
    }
}


@Composable
fun DrawLosingScreen(replayButton : () -> Unit){

    LaunchScreen(title = "YOU LOST!\nTRY AGAIN?", "Play", Color.Red) {
        replayButton()
    }

}









