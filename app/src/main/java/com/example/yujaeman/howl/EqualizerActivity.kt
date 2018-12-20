package com.example.yujaeman.howl
import android.content.Context
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.media.audiofx.DynamicsProcessing
import android.media.audiofx.Equalizer
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.example.yujaeman.howl.model.ContentDTO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_equalizer.*
import kotlinx.android.synthetic.main.item_comment.view.*
import java.lang.Exception

class EqualizerActivity() : AppCompatActivity() {

    var equalizer : Equalizer? = null
    var mediaPlayer : MediaPlayer? = null
    //@RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equalizer)

        mediaPlayer = MediaPlayer()
        equalizer = Equalizer(0,mediaPlayer!!.audioSessionId)
        //equalizer?.enabled

        text_one.text = (equalizer!!.getCenterFreq(0)/1000).toString() + "Hz"
        text_two.text = (equalizer!!.getCenterFreq(1)/1000).toString() + "Hz"
        text_three.text = (equalizer!!.getCenterFreq(2)/1000).toString() + "Hz"
        text_four.text =(equalizer!!.getCenterFreq(3)/1000).toString() + "Hz"
        text_five.text = (equalizer!!.getCenterFreq(4)/1000).toString() + "Hz"


        BarSet()


        Log.d("message","before")
        Bar_one.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                Log.d("message","before1")
             equalizer?.setBandLevel(0, i.toShort())
                Log.d("message","after")
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
        Bar_two.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, i: Int, fromUser: Boolean) {
                equalizer?.setBandLevel(1,i.toShort())
           }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        Bar_three.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, i: Int, fromUser: Boolean) {
                equalizer?.setBandLevel(2,i.toShort())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        Bar_four.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, i: Int, fromUser: Boolean) {
                equalizer?.setBandLevel(3,i.toShort())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        Bar_five.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, i: Int, fromUser: Boolean) {
                equalizer?.setBandLevel(4,i.toShort())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        equalizer?.setEnabled(true)
    }


    //@RequiresApi(Build.VERSION_CODES.O)
    fun  BarSet()
    {
        Bar_one.max=100
        //Bar_one.min = 0

        Bar_two.max = 100
        //Bar_two.min = 0

        Bar_three.max = 100
        //Bar_three.min = 0

        Bar_four.max = 100
        //Bar_four.min = 0

        Bar_five.max = 100
        //Bar_five.min = 0

    }

}
