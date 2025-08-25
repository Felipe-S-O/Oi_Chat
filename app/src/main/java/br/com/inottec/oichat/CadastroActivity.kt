package br.com.inottec.oichat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.inottec.oichat.databinding.ActivityCadastroBinding
import br.com.inottec.oichat.model.Usuario
import br.com.inottec.oichat.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

    //Configurando o binding
    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String

    private val firebaseAuth by lazy {
        //Instanciando o Firebase Auth
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        //Instanciando o Firebase Store
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //Configurando o binding
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //Configurando a Toolbar
        inicializarToolbar()
        // inicializarEventosDeClique()
        inicializarEventosDeClique()
    }

    private fun inicializarEventosDeClique() {
        //Configurando o evento de clique do botão cadastrar
        binding.buttonCadastrar.setOnClickListener {
            //Chamando a função de validar campos
            if (validarCampos()) {
                cadastrarUsuario(nome, email, senha)
            }
        }
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { resultado ->
                if (resultado.isSuccessful) {
                    //Salvar dados do usuário no Firestore
                    //id, nome, email, foto
                    val idUsuario = resultado.result.user?.uid
                    if (idUsuario != null) {
                        val usuario = Usuario(
                            id = idUsuario,
                            nome = nome,
                            email = email,
                            foto = ""
                        )
                        salvarUsuarioFirestore(usuario)
                    }
                }
            }.addOnFailureListener { erro ->
                try {
                    throw erro
                } catch (erroSenhaFraca: FirebaseAuthWeakPasswordException) {
                    erroSenhaFraca.printStackTrace()
                    exibirMensagem("Senha fraca, digite uma senha com mais caracteres especiais")

                } catch (erroEmailExistente: FirebaseAuthUserCollisionException) {
                    erroEmailExistente.printStackTrace()
                    exibirMensagem("E-mail já cadastrado")
                } catch (erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException) {
                    erroCredenciaisInvalidas.printStackTrace()
                    exibirMensagem("E-mail inválido")
                }
            }
    }

    private fun salvarUsuarioFirestore(usuario: Any) {
        firestore.collection("usuarios")
            .document(usuario.toString())
            .set(usuario)
            .addOnSuccessListener {
                exibirMensagem("Usuário cadastrado com sucesso")

                //Chamando tela principal
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
            }.addOnFailureListener {
                exibirMensagem("Erro ao cadastrar usuário")
            }
    }

    private fun validarCampos(): Boolean {
        nome = binding.editNome.text.toString()
        email = binding.editEmail.text.toString()
        senha = binding.editSenha.text.toString()

        //Verificando se os campos estão vazios
        if (!nome.isEmpty()) {

            //Configurando o erro do TextInputLayout
            binding.textInputLayoutNome.error = null
            if (!email.isEmpty()) {

                binding.textInputLayoutEmail.error = null
                if (!senha.isEmpty()) {
                    binding.textInputLayoutSenha.error = null
                    return true
                } else {
                    binding.textInputLayoutSenha.error = "Digite sua senha"
                    return false
                }
            } else {
                binding.textInputLayoutEmail.error = "Digite seu e-mail"
                return false
            }
        } else {
            binding.textInputLayoutNome.error = "Digite seu nome"
            return false
        }
    }

    private fun inicializarToolbar() {
        //Configurando a Toolbar
        val toolbar = binding.includeToolbar.tbPrincipal
        //Setando a Toolbar como a barra de ação
        setSupportActionBar(toolbar)
        //Configurando o titulo da Toolbar
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}



