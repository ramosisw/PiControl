package mx.itson.picontrol.vistas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import mx.itson.picontrol.R;
import mx.itson.picontrol.threads.ClientThread;

public class Control extends ActionBarActivity implements View.OnTouchListener,View.OnClickListener, ClientThread.Listener {
    ClientThread thread;
    private int PORT = 0;
    private String HOST = null;
    private ProgressDialog connectProgress;
    private EditText et_comando;
    private SeekBar seek_pwm;
    int pwmValue=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        Intent intent = getIntent();
        PORT = intent.getIntExtra("PORT",/*defaltvalue*/ 0);
        HOST = intent.getStringExtra("HOST");

        connectProgress = ProgressDialog.show(this, "Por favor espere ...", "Conectando ...", true);
        connectProgress.setCancelable(false);

        thread = new ClientThread(HOST, PORT);
        thread.setListener(this);
        thread.start();
        init();

    }

    private void init() {
        et_comando=(EditText)findViewById(R.id.et_comando);
        seek_pwm = (SeekBar) findViewById(R.id.seek_pwm);
        seek_pwm.setEnabled(false);
        seek_pwm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                thread.sendCommand("P"+progress);
                pwmValue=progress;
            }
        });
        int toggleBtns[] = {
                R.id.btn_a, R.id.btn_b, R.id.btn_c,R.id.btn_seek,R.id.btn_enviar
        };
        //Inicializar ToogleEvent
        for(int id : toggleBtns){
            findViewById(id).setOnClickListener(this);
        }
    }


    @Override
    protected void onStop() {
        this.thread.closeConnection();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        thread.closeConnection();
        thread.interrupt();
        super.onBackPressed();
    }

    @Override
    public void onReceiveMessage(String message) {
        viewToast(message);
    }

    @Override
    public void onDisconnected(String message) {
        viewToast(message);
        finish();
    }

    @Override
    public void onConnected() {
        connectProgress.dismiss();
    }

    @Override
    public void onSendMessage() {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    private void viewToast(final String message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(Control.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_a:
                if(((ToggleButton)v).isChecked()) {
                    thread.sendCommand("A1");
                }else {
                    thread.sendCommand("A0");
                }
                break;
            case R.id.btn_b:
                if(((ToggleButton)v).isChecked()) {
                    thread.sendCommand("B1");
                }else {
                    thread.sendCommand("B0");
                }
                break;
            case R.id.btn_c:
                if(((ToggleButton)v).isChecked()) {
                    thread.sendCommand("C1");
                }else {
                    thread.sendCommand("C0");
                }
                break;
            case R.id.btn_seek:
                if(((ToggleButton)v).isChecked()) {
                    seek_pwm.setEnabled(true);
                    thread.sendCommand("P"+pwmValue);
                }else {
                    seek_pwm.setEnabled(false);
                    thread.sendCommand("P0");
                }
                break;
            case R.id.btn_enviar:
                String comando=et_comando.getText().toString().trim();
                if(comando==null || comando.isEmpty() || comando.length()==0) {
                    et_comando.setError("Este campo no puede estar vacio");
                }else{
                    thread.sendCommand(comando);
                }
                break;
        }
    }
}
