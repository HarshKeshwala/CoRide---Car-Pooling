package harsh.keshwala.com.coride;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AdminLoginActivity extends AppCompatActivity {


    private EditText id,password;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        id = (EditText) findViewById(R.id.adminId);
        password = (EditText) findViewById(R.id.adminPassword);

        login = (Button) findViewById(R.id.riderLoginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id.getText().toString().equals("admin") == true && password.getText().toString().equals("admin") == true) {
                    startActivity(new Intent(AdminLoginActivity.this,AdminHomeActivity.class));
                }
            }
        });
    }
}
