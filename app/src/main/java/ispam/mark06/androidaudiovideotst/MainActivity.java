package ispam.mark06.androidaudiovideotst;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {
    private VideoView videoView;
    private Button btnPlayVideo, btnPlayMusic, btnPauseMusic;
    private SeekBar seekBarVolume;
    private SeekBar seekBarMove;

    private MediaController mediaController;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);

        btnPlayVideo = findViewById(R.id.btnPlayVideo);
        btnPlayMusic = findViewById(R.id.btnPlayMusic);
        btnPauseMusic = findViewById(R.id.btnPauseMusic);

        seekBarVolume = findViewById(R.id.seekBarVolume);
        seekBarMove = findViewById(R.id.seekBarMove);


        mediaController = new MediaController(MainActivity.this);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.song);
        mediaPlayer.setOnCompletionListener(this);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        int minVol = audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC);

        seekBarVolume.setMax(maxVol);
        seekBarVolume.setProgress(currVol);

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    Toast.makeText(MainActivity.this, progress+"", Toast.LENGTH_SHORT).show();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        seekBarMove.setOnSeekBarChangeListener(this);
        seekBarMove.setMax(mediaPlayer.getDuration());

        btnPlayVideo.setOnClickListener(this);
        btnPlayMusic.setOnClickListener(this);
        btnPauseMusic.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnPlayVideo:
                Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.mercvid);
                videoView.setVideoURI(uri);
                videoView.setMediaController(mediaController);
                mediaController.setAnchorView(videoView);
                videoView.start();
                break;
            case R.id.btnPlayMusic:
                mediaPlayer.start();
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        seekBarMove.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }, 0, 1000);
                break;

            case R.id.btnPauseMusic:
                mediaPlayer.pause();
                timer.cancel();
                break;
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
//            Toast.makeText(this, progress+"", Toast.LENGTH_SHORT).show();
//            seekBarMove.setProgress(progress);
            mediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mediaPlayer.pause();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mediaPlayer.start();
    }


    //if music ends >>
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        timer.cancel();
        Toast.makeText(this, "Music Finished", Toast.LENGTH_SHORT).show();
    }
}
