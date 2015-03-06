package mx.itson.picontrol.vistas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mx.itson.picontrol.R;

public class Startup extends ActionBarActivity {
    Button btn_conectar;
    EditText et_host, et_port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        init();
    }

    private void init() {
        et_host = (EditText) findViewById(R.id.et_host);
        et_port = (EditText) findViewById(R.id.et_port);
        btn_conectar = (Button) findViewById(R.id.btn_conectar);
        btn_conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClick_btn_conectar(v);
            }
        });
    }

    private void OnClick_btn_conectar(View v) {
        String host=et_host.getText().toString().trim();
        if(host.isEmpty() || host==null || host.length()==0) {
            et_host.setError("El campo \"HOST\" no puede estar vacio");
            return;
        }
        int port;
        try {
            port = Integer.parseInt(et_port.getText().toString().trim());
        }catch(Exception ex) {
            et_port.setError("El campo \"PORT\" No es un numero entero");
            return;
        }
        Intent control = new Intent(this, Control.class);
        control.putExtra("PORT",port);
        control.putExtra("HOST",host);
        startActivity(control);
    }



}
