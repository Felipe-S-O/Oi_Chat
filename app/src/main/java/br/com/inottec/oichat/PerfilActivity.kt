package br.com.inottec.oichat


import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import br.com.inottec.oichat.databinding.ActivityPerfilBinding
import br.com.inottec.oichat.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class PerfilActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }

    private var temPermissaoGaleria = false
    private var temPermissaoCamera = false

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }


    override fun onStart() {
        super.onStart()
        recuperarDadosUsuario()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Configurando a Toolbar
        inicializarToolbar()

        //Solicitar permição de galeria e camera
        solicitarPermissoes()

        //Configurando os eventos de clique
        inicializarEventosCliques()

    }


    private fun recuperarDadosUsuario() {
        val idUsuario = firebaseAuth.currentUser?.uid

        if (idUsuario != null) {
            firestore
                .collection("usuarios")
                .document(idUsuario)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val dadosUsuario = documentSnapshot.data

                    if (!dadosUsuario.isNullOrEmpty()) {
                        val nomeUsuario = dadosUsuario["nome"].toString()
                        val fotoUsuario = dadosUsuario["foto"].toString()

                        binding.editaPefil.setText(nomeUsuario)
                        if (!fotoUsuario.isEmpty()) {
                            Picasso.get()
                                .load(fotoUsuario)
                                .into(binding.imageView2)
                        }
                    }
                }
        }
    }


    //setando imagem da galeria na imageview
    private val galeria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            binding.imageView2.setImageURI(uri)
            uploadImagemStorage(uri)
        } else {
            exibirMensagem("Nenhuma imagem selecionada")
        }
    }

    private fun uploadImagemStorage(uri: Uri) {
        // foto -> usuario -> idUsuario -> perfil.jpg
        val idUsuario = firebaseAuth.currentUser?.uid
        if (idUsuario != null) {
            storage
                .getReference("foto")
                .child("usuarios")
                .child("id")
                .child("perfil.jpg")
                .putFile(uri)
                .addOnSuccessListener { task ->
                    exibirMensagem("Imagem enviada com sucesso")
                    task.metadata
                        ?.reference
                        ?.downloadUrl
                        ?.addOnSuccessListener { uri ->
                            //salvando a url no firestore
                            val dados = mapOf(
                                "foto" to uri.toString(),
                            )

                            atualizarDadosPerfil(idUsuario, dados)

                        }
                }.addOnFailureListener {
                    exibirMensagem("Erro ao enviar imagem")
                }
        }
    }

    private fun atualizarDadosPerfil(
        idUsuario: String,
        dados: Map<String, Any>
    ) {
            firestore.collection("usuarios")
                .document(idUsuario)
                .update(dados)
                .addOnSuccessListener {
                    exibirMensagem("Perfil atualizado com sucesso")
                }
                .addOnFailureListener { e ->
                    exibirMensagem("Erro ao atualizar perfil")
                }
    }


    //setando imagem da galeria na imageview
    private fun abrirGaleria() {
        galeria.launch("image/*")
    }

    private fun inicializarEventosCliques() {
        binding.floatingActionButton.setOnClickListener {
            if (temPermissaoGaleria) {
                abrirGaleria()
            } else {
                exibirMensagem("Permissão de galeria negada")
                solicitarPermissoes()
            }
        }

        binding.buttonAtualizarPerfil.setOnClickListener {
            val nomeUsuario = binding.editaPefil.text.toString()

            if (!nomeUsuario.isEmpty()) {
                val idUsuario = firebaseAuth.currentUser?.uid

                if (idUsuario != null) {
                    val dados = mapOf(
                        "nome" to binding.editaPefil.text.toString()
                    )
                    atualizarDadosPerfil(idUsuario, dados)
                }
            } else {
                exibirMensagem("Preencha o campo nome")
            }
        }
    }

    //Solicitar multiplas permissões
    private val gerenciadorPermissoes = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissoes ->
        temPermissaoCamera = permissoes[Manifest.permission.CAMERA] ?: false
        temPermissaoGaleria = permissoes[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
    }

    private fun solicitarPermissoes() {
        //Verifica se usuário já tem permissão
        temPermissaoCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED


        temPermissaoGaleria = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        //Lista de permissões negadas
        val listaPermissoesNegadas = mutableListOf<String>()
        if (!temPermissaoCamera)
            listaPermissoesNegadas.add(Manifest.permission.CAMERA)

        if (!temPermissaoGaleria)
            listaPermissoesNegadas.add(Manifest.permission.READ_EXTERNAL_STORAGE)


        if (listaPermissoesNegadas.isNotEmpty())
            gerenciadorPermissoes.launch(listaPermissoesNegadas.toTypedArray())


    }

    private fun inicializarToolbar() {
        //Configurando a Toolbar
        val toolbar = binding.includeToolbarPerfil.tbPrincipal
        //Setando a Toolbar como a barra de ação
        setSupportActionBar(toolbar)
        //Configurando o titulo da Toolbar
        supportActionBar?.apply {
            title = "Perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}




