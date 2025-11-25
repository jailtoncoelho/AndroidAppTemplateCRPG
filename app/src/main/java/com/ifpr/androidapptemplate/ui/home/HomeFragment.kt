package com.ifpr.androidapptemplate.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.util.Base64
import android.widget.*
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ifpr.androidapptemplate.baseclasses.Item
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ifpr.androidapptemplate.ui.ai.AiLogicActivity
import com.ifpr.androidapptemplate.ui.ai.AiLogicFragment
import com.ifpr.androidapptemplate.R
import com.ifpr.androidapptemplate.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val container = view.findViewById<LinearLayout>(R.id.itemContainer)
        carregarItensMarketplace(container)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab_ai)

        fab.setOnClickListener {
            val context = view.context
            val intent = Intent(context, AiLogicActivity::class.java)
            context.startActivity(intent)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun carregarItensMarketplace(container: LinearLayout) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("itens")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                container.removeAllViews()

                for (userSnapshot in snapshot.children) {
                    for (itemSnapshot in userSnapshot.children) {
                        val item = itemSnapshot.getValue(Item::class.java) ?: continue

                        val itemView = LayoutInflater.from(container.context)
                            .inflate(R.layout.item_template, container, false)

                        val imageView = itemView.findViewById<ImageView>(R.id.item_image)
                        val enderecoView = itemView.findViewById<TextView>(R.id.item_endereco)
                        val tipoTreino = itemView.findViewById<TextView>(R.id.tipoTreinoEditText)
                        val dataTreino = itemView.findViewById<TextView>(R.id.dataTreinoEditText)
                        val recordTreino = itemView.findViewById<TextView>(R.id.recordTreinoEditText)
                        val obsTreino = itemView.findViewById<TextView>(R.id.obsTreinoEditText)

                        enderecoView.text = "Endereço: ${item.endereco ?: "Não informado"}"
                        tipoTreino.text = "Tipo de Treino: ${item.tipoTreinoEditText ?: "Não informado"}"
                        dataTreino.text = "Data do Treino: ${item.dataTreinoEditText ?: "Não informado"}"
                        recordTreino.text = "Record/Marca: ${item.tipoTreinoEditText ?: "Não informado"}"
                        obsTreino.text = "Observações: ${item.obsTreinoEditText ?: "Não informado"}"



                        if (!item.imageUrl.isNullOrEmpty()) {
                            Glide.with(container.context).load(item.imageUrl).into(imageView)
                        } else if (!item.base64Image.isNullOrEmpty()) {
                            try {
                                val bytes = Base64.decode(item.base64Image, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                imageView.setImageBitmap(bitmap)
                            } catch (_: Exception) {}
                        }

                        container.addView(itemView)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(container.context, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
            }
        })
    }
}