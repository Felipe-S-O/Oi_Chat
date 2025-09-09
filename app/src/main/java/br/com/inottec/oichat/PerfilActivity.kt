package br.com.inottec.oichat


import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import br.com.inottec.oichat.databinding.ActivityPerfilBinding
import br.com.inottec.oichat.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class PerfilActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }

    private var temPermissaoGaleria = false
    private var temPermissaoCamera = false

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }

    //setando imagem da galeria na imageview
    private val galeria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null){
            binding.imageView2.setImageURI(uri)
            uploadImagemStorage(uri)
        }else{
            exibirMensagem("Nenhuma imagem selecionada")
        }
    }

    private fun uploadImagemStorage(uri: Uri) {
        // foto -> usuario -> idUsuario -> perfil.jpg
        val idUsuario = firebaseAuth.currentUser?.uid
        if (idUsuario != null){

        storage
            .getReference("foto")
            .child("usuario")
            .child("id")
            .child("perfil.jpg")
            .putFile(uri)
            .addOnSuccessListener {
                exibirMensagem("Imagem enviada com sucesso")
            }
            .addOnFailureListener {
                exibirMensagem("Erro ao enviar imagem")
            }
        }
    }

    //setando imagem da galeria na imageview
    private fun abrirGaleria() {
        galeria.launch("image/*")
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
    private fun inicializarEventosCliques() {
        binding.floatingActionButton.setOnClickListener {
            if (temPermissaoGaleria){
                abrirGaleria()
            }else{
                exibirMensagem("Permissão de galeria negada")
                solicitarPermissoes()
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


