package com.cecilia.cassab.sensorapp2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.app.Activity;
public class MainActivity extends Activity {

    double luz;
    double dist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent it = getIntent();
        luz = it.getDoubleExtra("luz",0);
        dist = it.getDoubleExtra("dist",0);
        Log.i("cheguei","luz="+luz+" dist="+dist);

    }

    public void DevolverClass(View v){
        String classLuz;
        String classDist;

        Intent it = new Intent();

        Log.i("cheguei","devolvendo luz="+luz+" dist="+dist);

        if(luz<20)
            classLuz = "baixa";
        else
            classLuz = "alta";
        if(dist<3)
            classDist = "perto";
        else
            classDist = "longe";

        it.putExtra("classL",classLuz);
        it.putExtra("classD",classDist);

        setResult(RESULT_OK,it);
        finish();
    }
}