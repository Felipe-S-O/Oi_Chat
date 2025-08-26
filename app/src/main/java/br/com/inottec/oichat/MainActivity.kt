package br.com.inottec.oichat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.inottec.oichat.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        //Instanciando o Firebase Auth
        FirebaseAuth.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Configurando a Toolbar
        inicializarToolbar()
    }

    private fun inicializarToolbar() {
        //Configurando a Toolbar
        val toolbar = binding.includeMainToolbar.tbPrincipal
        //Setando a Toolbar como a barra de ação
        setSupportActionBar(toolbar)
        //Configurando o titulo da Toolbar
        supportActionBar?.apply {
            title = "Oi Chat"
        }
    }
}