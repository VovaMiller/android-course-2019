package com.vovamiller_97.pioneer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private View cardView;
    private Button buttonSendMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.title_main);

        cardView = findViewById(R.id.cardView);
        buttonSendMail = findViewById(R.id.buttonSendMail);

        setListeners();
    }

    private void setListeners() {
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCardView();
            }
        });
        buttonSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButtonSendMail();
            }
        });
    }

    private void onClickCardView() {
        Intent infoIntent = new Intent(this, InfoActivity.class);

        startActivity(infoIntent);
    }

    private void onClickButtonSendMail() {
        String mailto = "mailto:vovamiller_97@mail.ru" +
                "?subject=" + Uri.encode("Notes feedback");

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));

        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            Context context = getApplicationContext();
            CharSequence text = "Can't find any EMail app!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }
}
