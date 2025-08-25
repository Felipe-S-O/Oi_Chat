package br.com.inottec.oichat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.inottec.oichat.databinding.ActivityLoginBinding
import br.com.inottec.oichat.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var email: String
    private lateinit var senha: String

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarEventosClique()
    }

    //verificar se o usuário está logado na inicialização da tela
    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {
        val usuarioAtual = firebaseAuth.currentUser
        if (usuarioAtual != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    //TODO: Validar campos

    private fun inicializarEventosClique() {
        binding.textCadastro.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        binding.buttonEntrar.setOnClickListener {
            if (validarCampos()) {
                logarUsuario()
            }
        }

    }

    private fun validarCampos(): Boolean {
        email = binding.editLoginEmail.text.toString()
        senha = binding.editLoginSenha.text.toString()

        if (!email.isEmpty()) {
            binding.textInputLayoutLoginEmail.error = null

            if (!senha.isEmpty()) {
                binding.textInputLayoutLoginSenha.error = null
                return true
            } else {
                binding.textInputLayoutLoginSenha.error = "Digite sua senha"
                return false
            }

        } else {
            binding.textInputLayoutLoginEmail.error = "Digite seu e-mail"
            return false
        }
    }

    private fun logarUsuario() {
        firebaseAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener { resultado ->
            if (resultado.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }.addOnFailureListener {

        }

        firebaseAuth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener {
                exibirMensagem("Usuário logado com sucesso")
                //Chamando tela principal
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
            }
            .addOnFailureListener { error ->
                try {
                    throw error
                } catch (erroUsuarioInvalido: FirebaseAuthInvalidUserException) {
                    erroUsuarioInvalido.printStackTrace()
                    exibirMensagem("E-mail não cadastrado")
                } catch (erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException) {
                    erroCredenciaisInvalidas.printStackTrace()
                    exibirMensagem("E-mail ou senha incorretos")
                }
            }
    }

}





