package edu.msu.team2.capturetheflag;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public String MyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private EditText getUsernameEditText() {
        return (EditText)findViewById(R.id.usernameEditText);
    }

    public void onOkay(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        MyName = getNameEditText().getText().toString();
        intent.putExtra("name", MyName);
        startActivity(intent);
    }

    public void onCancel(View view){
        onBackPressed();
    }

    private EditText getNameEditText() {
        return (EditText) findViewById(R.id.usernameEditText);
    }
}
