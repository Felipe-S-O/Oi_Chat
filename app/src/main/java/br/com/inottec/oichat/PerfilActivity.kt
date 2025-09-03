package br.com.inottec.oichat


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import br.com.inottec.oichat.databinding.ActivityPerfilBinding

class PerfilActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }

    private var temPermissaoGaleria = false
    private var temPermissaoCamera = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Configurando a Toolbar
        inicializarToolbar()

        //Solicitar permição de galeria e camera
        solicitarPermissoes()

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