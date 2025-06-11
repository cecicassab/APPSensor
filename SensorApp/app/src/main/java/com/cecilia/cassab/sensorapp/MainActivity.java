package com.cecilia.cassab.sensorapp;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensorLuz;
    private Sensor sensorVibra;
    LanternaHelper lanternaHelper;
    MotorHelper motorHelper;
    double luz;
    double dist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorLuz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorVibra = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        lanternaHelper = new LanternaHelper(this);
        motorHelper = new MotorHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sensorLuz != null){
            sensorManager.registerListener(this,sensorLuz, SensorManager.SENSOR_DELAY_GAME);
        }
        if(sensorVibra != null){
            sensorManager.registerListener(this,sensorVibra, SensorManager.SENSOR_DELAY_GAME);
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        //desliga vibração e luz ao fechar o app
        motorHelper.pararVibracao();
        lanternaHelper.desligar();
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor s = event.sensor; //obtem o sensor que mudou
        Log.i("SENSOR_MUDOU", "Tipo: " + s.getStringType());

        if (s.getType() == Sensor.TYPE_PROXIMITY) {
            Log.i("SENSOR_MUDOU", "Proximidade: " + event.values[0] + " cm.");
            dist = event.values[0];
        }
        else if(s.getType() == Sensor.TYPE_LIGHT){
            Log.i("SENSOR_MUDOU", "Luz: " + event.values[0] + "lx.");
            luz = event.values[0];
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        String prec = "";
        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                prec = "Baixa";
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                prec = "Média";
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                prec = "Alta";
                break;
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                prec = "Sinal indisponível – não confiável";
                break;            default:
        }
        Log.i("SENSOR_PRECISAO", "NOME: " + sensor.getName() + " Precisão: " + prec);
    }

    public void ClassReadings(View v){
        Intent it = new Intent("LEVASENSOR");
        it.addCategory("ACAO_LEVASENSOR_CECI");
        it.putExtra("luz", luz);
        it.putExtra("dist", dist);
        startActivityForResult(it, 1);
    }

    @Override
    protected void onActivityResult(int codigoRequisicao, int codigoResultado, Intent it){
        Log.i("voltei","voltei do app 2");
        if(codigoRequisicao == 1 && codigoResultado == RESULT_OK && it != null){
            String classL = it.getStringExtra("classL");
            String classD = it.getStringExtra("classD");
            Log.i("voltei","dist="+classD+" luz="+classL);

            SwitchCompat switchLuz = findViewById(R.id.switchLantern);
            SwitchCompat switchVibra = findViewById(R.id.switchMotor);

            assert classL != null;
            if(classL.equals("baixa")){
                lanternaHelper.ligar();
                switchLuz.setChecked(true);
            }
            else{
                lanternaHelper.desligar();
                switchLuz.setChecked(false);
            }

            assert classD != null;
            if(classD.equals("longe")){
                motorHelper.iniciarVibracao();
                switchVibra.setChecked(true);
            }
            else{
                motorHelper.pararVibracao();
                switchVibra.setChecked(false);
            }
        }
        else{
            Toast.makeText(this,"Nehuma classificação",Toast.LENGTH_SHORT).show();
        }
    }
}
