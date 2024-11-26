package com.example.medicare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class Sign_upActivity extends AppCompatActivity {

    EditText edusername,edpassword,edrepassword;
    Button btn;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edusername = findViewById(R.id.username);
        edpassword = findViewById(R.id.password);
        edrepassword = findViewById(R.id.repassword);
        btn = findViewById(R.id.btnsignup);
        tv = findViewById(R.id.other);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Sign_upActivity.this, LoginActivity.class));
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edusername.getText().toString();
                String password = edpassword.getText().toString();
                String repassword = edrepassword.getText().toString();
                Database db = new Database(getApplicationContext(),"Medicare",null,1);
                if (username.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
                    Toast.makeText(Sign_upActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (!password.equals(repassword)) {
                        Toast.makeText(Sign_upActivity.this, "Password and repassword do not match", Toast.LENGTH_SHORT).show();
                    } else if (!isValid(password)) {
                        Toast.makeText(Sign_upActivity.this, "Password must contain at least one letter, one number, and one special character", Toast.LENGTH_SHORT).show();
                    } else {
                        // Proceed with signup logic
                        db.Sign_up(username,password);
                        Toast.makeText(Sign_upActivity.this, "Ready to go.....", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Sign_upActivity.this, LoginActivity.class));
                    }
                }
            }
        });
    }
    public static boolean isValid(String passwordhere) {
        if (passwordhere.length() < 6) { // Minimum length for password
            return false;
        }

        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : passwordhere.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (isSpecialCharacter(c)) {
                hasSpecialChar = true;
            }
        }

        return hasLetter && hasDigit && hasSpecialChar;
    }

    private static boolean isSpecialCharacter(char c) {
        return (c >= 33 && c <= 47) || (c >= 58 && c <= 64) || (c >= 91 && c <= 96) || (c >= 123 && c <= 126);
    }
}