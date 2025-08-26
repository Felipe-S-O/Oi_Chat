package br.com.inottec.oichat

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
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

        //adicionar menu
        addMenuProvider(
            //MenuProvider
            object : MenuProvider {
                override fun onCreateMenu(
                    menu: Menu,
                    menuInflater: MenuInflater
                ) {
                    //inflar o menu
                    menuInflater.inflate(R.menu.menu_principal, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    //verificar qual item foi clicado
                    when (menuItem.itemId) {
                        //quando o item for clicado
                        R.id.item_perfil -> {
                            startActivity(
                                Intent(applicationContext, PerfilActivity::class.java)
                            )

                        }
                        //quando o item for clicado
                        R.id.item_sair -> {
                            delogarUsuario()
                        }
                    }
                    return true
                }

                private fun delogarUsuario() {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Atenção")
                        .setMessage("Deseja realmente sair?")
                        .setNegativeButton("Não") { dialog, which -> }
                        .setPositiveButton("Sim") { dialog, which ->
                            firebaseAuth.signOut()
                            startActivity(
                                Intent(applicationContext, LoginActivity::class.java)
                            )
                        }
                        .create()
                        .show()
                }

            }
        )
    }
}