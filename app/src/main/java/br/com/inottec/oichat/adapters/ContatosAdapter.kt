package br.com.inottec.oichat.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.inottec.oichat.databinding.ItemContatosBinding
import br.com.inottec.oichat.model.Usuario
import com.squareup.picasso.Picasso

class ContatosAdapter : RecyclerView.Adapter<ContatosAdapter.ContatosViewHolder>() {

    private var listaContatos = emptyList<Usuario>()

    fun adicionarLista(lista: List<Usuario>) {
        this.listaContatos = lista
        notifyDataSetChanged()
    }

    inner  class  ContatosViewHolder(
        private val binding: ItemContatosBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(usuario: Usuario) {
            binding.textContatoNome.text = usuario.nome
            Picasso.get()
                .load(usuario.foto)
                .into(binding.imageContatoFoto)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContatosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
      val itemView = ItemContatosBinding.inflate(
          layoutInflater,
          parent,
          false
      )
        return ContatosViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: ContatosViewHolder,
        position: Int
    ) {
        val usuario = listaContatos[position]
        holder.bind(usuario)
    }

    override fun getItemCount(): Int {
        return listaContatos.size
    }


}