package br.com.inottec.oichat

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.inottec.oichat.databinding.ActivityMensagensBinding
import br.com.inottec.oichat.model.Mensagem
import br.com.inottec.oichat.model.Usuario
import br.com.inottec.oichat.utils.Constantes
import br.com.inottec.oichat.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class MensagensActivity : AppCompatActivity() {

    private val firebaseAuth by lazy {
        //Instanciando o Firebase Auth
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        //Instanciando o Firebase Store
        FirebaseFirestore.getInstance()
    }
    private val binding by lazy {
        ActivityMensagensBinding.inflate(layoutInflater)
    }
    private var dadosDestinatario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        recuperarDadosUsuarioDestinatario()
        inicializarToolbar()
        inicializarEventosDeClique()
    }

    private fun inicializarEventosDeClique() {
        binding.fabEnviar.setOnClickListener {
            //Recuperar a mensagem digitada
            val mensagem = binding.editMensagem.text.toString()
            if (mensagem.isNotEmpty()) {
                salvarMensagem(mensagem)
            }
        }
    }

    private fun inicializarToolbar() {
        //Configurando a Toolbar
        val toolbar = binding.tbMensagens
        //Setando a Toolbar como a barra de ação
        setSupportActionBar(toolbar)
        //Configurando o titulo da Toolbar
        supportActionBar?.apply {
            title = ""
            if (dadosDestinatario != null) {
                binding.textNome.text = dadosDestinatario!!.nome
                Picasso.get()
                    .load(dadosDestinatario!!.foto)
                    .into(binding.imageFotoPerfil)

            }
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun salvarMensagem(TextoMensagem: String) {
        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        val idUsuarioDestinatario = dadosDestinatario?.id

        if (idUsuarioRemetente != null && idUsuarioDestinatario != null) {
            val mensagem = Mensagem(
                idUsuarioRemetente,
                TextoMensagem
            )

            //Salvar para o remetente
            salvarMensagemFirestore(idUsuarioRemetente, idUsuarioDestinatario, mensagem)
            
            //Salvar para o destinatário
            salvarMensagemFirestore(idUsuarioDestinatario, idUsuarioRemetente, mensagem)

            binding.editMensagem.setText("")
        }
    }

    private fun salvarMensagemFirestore(
        idUsuarioRemetente: String,
        idUsuarioDestinatario: String,
        mensagem: Mensagem
    ) {
        firestore
            .collection(Constantes.BD_MENSAGENS)
            .document(idUsuarioRemetente)
            .collection(idUsuarioDestinatario)
            .add(mensagem)
            .addOnFailureListener {
                exibirMensagem("Erro ao enviar mensagem")
            }
    }

    private fun recuperarDadosUsuarioDestinatario() {
        val extras = intent.extras
        if (extras != null) {
            val origem = extras.getString("origem")
            if (origem == Constantes.ORIGEM_CONTATO) {

                dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable("dadosDestinatario", Usuario::class.java)
                } else {
                    extras.getParcelable("dadosDestinatario")
                }
            } else if (origem == Constantes.ORIGEM_CONVERSAO) {

            }
        }
    }
}


