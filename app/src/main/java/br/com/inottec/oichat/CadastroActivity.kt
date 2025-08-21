package br.com.inottec.oichat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.inottec.oichat.databinding.ActivityCadastroBinding
class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarToolbar()

        inicializarEventosDeClique()
    }

    private fun validarCampos(): Boolean {
        if(nome.isEmpty()){
            if(email.isEmpty()){
                return true
                if (senha.isEmpty()) {
                    return true
                }else{
                    binding.textInputLayoutSenha.error = "Digite sua senha"
                    return false
                }
            }else{
                binding.textInputLayoutEmail.error = "Digite seu e-mail"
                return false
            }
            return true
        }else{
            binding.textInputLayoutNome.error = "Digite seu nome"
            return false
        }
    }

    private fun inicializarEventosDeClique() {
        binding.buttonCadastrar.setOnClickListener {
            if( validarCampos(){

                })
        }
    }

    private fun inicializarToolbar() {
       val toolbar = binding.includeToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}