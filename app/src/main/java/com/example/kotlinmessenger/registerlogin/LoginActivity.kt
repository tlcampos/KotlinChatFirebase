package com.example.kotlinmessenger.registerlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.messages.LatestMessagesActivity
import com.example.kotlinmessenger.messages.NewMessageActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email = email_edit.text.toString()
        val password = edit_password.text.toString()

        login_button_login.setOnClickListener(this)
        register_account_text_view.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view == null) return
        val id: Int = view.id
        if (id == R.id.login_button_login) {
            validationLogin()
        } else if (id == R.id.register_account_text_view) {
            Log.d("MainActivity", "Mostre a activity de registro")
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        } else {

        }
    }

    private fun validationLogin() {
        val email = email_edit.text.toString()
        val password = edit_password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Usuário e senha devem ser preenchidos", Toast.LENGTH_SHORT)
                .show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("LoginActivity","Login efetuado com sucesso com usuário: ${it.result?.user?.uid}"
                    )

                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                Log.d("LoginActivity", "Falha ao tentar realizar o login")
            }
    }
}
