package br.com.inottec.oichat.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.inottec.oichat.R
import br.com.inottec.oichat.adapters.ContatosAdapter
import br.com.inottec.oichat.databinding.ActivityPerfilBinding
import br.com.inottec.oichat.databinding.FragmentContatosBinding
import br.com.inottec.oichat.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ContatosFragment : Fragment() {

    private lateinit var binding: FragmentContatosBinding
    private lateinit var eventoSnapshot: ListenerRegistration
    private lateinit var adapter: ContatosAdapter

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentContatosBinding.inflate(inflater, container, false)

        adapter = ContatosAdapter()
        binding.rvContatos.adapter = adapter
        binding.rvContatos.layoutManager = LinearLayoutManager(context)
        binding.rvContatos.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        return binding.root

    }

    override fun onStart() {
        super.onStart()
        adicionarListenerContatos()
    }

    private fun adicionarListenerContatos() {

        eventoSnapshot = firestore
            .collection("usuarios")
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val listaContatos = mutableListOf<Usuario>()
                val documents = querySnapshot?.documents

                documents?.forEach { documentoSnapshot ->
                    //Recuperando os dados do usuário
                    val usuario = documentoSnapshot.toObject(Usuario::class.java)

                    //Verificando se o usuário é o usuário logado
                    if (usuario != null && usuario.id != firebaseAuth.currentUser?.uid) {
                        listaContatos.add(usuario)
                    }
                }

                if (listaContatos.isNotEmpty()) {
                    adapter.adicionarLista(listaContatos)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }
}