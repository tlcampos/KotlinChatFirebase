package com.example.kotlinmessenger.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.messages.LatestMessagesActivity
import com.example.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button_register.setOnClickListener {
            performRegister()
        }

        selectphoto_button_register.setOnClickListener {
            Log.d("RegisterActivity", "Selecão de foto")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null)
        //Processa e verifica qual imagem foi selecionada
            Log.d("RegisterActivity", "Photo foi selecionada")

        selectedPhotoUri = data!!.data

        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

        selectphoto_imageview_register.setImageBitmap(bitmap)

        selectphoto_button_register.alpha = 0f

    }

    private fun performRegister() {
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this, "Senha e ou e-mail devem ser informado!", Toast.LENGTH_SHORT
            ).show()
            return
        }
        Log.d("MainActivity", "Email é: $email")
        Log.d("MainActivity", "Senha é: $password")

        //Criação do usuário no Firebase autentication com usuário e senha
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                // else if successful
                Log.d("RegisterActivity","Usuário criado com sucesso com UID: ${it.result?.user?.uid}"
                )

                uploadImageToFireBaseStorage()
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Falha ao criar usuário: ${it.message}")
                Toast.makeText(this, "Falha ao criar usuário: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun uploadImageToFireBaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(
                    "RegisterActivity",
                    "Upload de imagem feita com sucesso: ${it.metadata?.path}"
                )

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d("RegisterActivity", "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Falha ao realizar Upload imagem")
            }

    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid!!, username_edittext_register.text.toString(), profileImageUrl)

        val intent = Intent(this, LatestMessagesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Usuário salvo no banco de dados")
            }
    }

}