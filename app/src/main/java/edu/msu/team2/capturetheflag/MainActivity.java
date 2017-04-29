package edu.msu.team2.capturetheflag;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private EditText getUsernameEditText() {
        return (EditText)findViewById(R.id.usernameEditText);
    }
}
