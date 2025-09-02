package br.com.inottec.oichat


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import br.com.inottec.oichat.databinding.ActivityPerfilBinding

class PerfilActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }
//
//    private var temPermissaoGaleria = false
//    private var temPermissaoCamera = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Configurando a Toolbar
        inicializarToolbar()

        //Solicitar permição de galeria e camera
//        solicitarPermissoes()

    }

//    private fun solicitarPermissoes() {
//        //Verifica se usuário já tem permissão
//        temPermissaoCamera = ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.CAMERA
//        ) == PackageManager.PERMISSION_GRANTED
//
//    }

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