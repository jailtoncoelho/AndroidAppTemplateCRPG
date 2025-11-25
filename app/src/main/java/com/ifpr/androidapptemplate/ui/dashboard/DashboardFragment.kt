package com.ifpr.androidapptemplate.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.ifpr.androidapptemplate.R
import com.ifpr.androidapptemplate.baseclasses.Item
import com.ifpr.androidapptemplate.databinding.FragmentDashboardBinding


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private var imageUri: Uri? = null


    //TODO("Declare aqui as outras variaveis do tipo EditText que foram inseridas no layout")
    private lateinit var enderecoEditText: EditText
    private lateinit var tipoTreinoEditText: EditText
    private lateinit var dataTreinoEditText: EditText
    private lateinit var recordTreinoEditText: EditText
    private lateinit var obsTreinoEditText: EditText
    private lateinit var itemImageView: ImageView
    private lateinit var salvarButton: Button
    private lateinit var selectImageButton: Button
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        itemImageView = view.findViewById(R.id.image_item)
        salvarButton = view.findViewById(R.id.salvarItemButton)
        selectImageButton = view.findViewById(R.id.button_select_image)
        enderecoEditText = view.findViewById(R.id.enderecoItemEditText)
        tipoTreinoEditText = view.findViewById(R.id.tipoTreinoEditText)
        dataTreinoEditText = view.findViewById(R.id.dataTreinoEditText)
        recordTreinoEditText = view.findViewById(R.id.recordTreinoEditText)
        obsTreinoEditText = view.findViewById(R.id.obsTreinoEditText)
        //TODO("Capture aqui os outro campos que foram inseridos no layout. Por exemplo, ate
        // o momento so foi capturado o endereco (EditText)")

        auth = FirebaseAuth.getInstance()

        selectImageButton.setOnClickListener {
            openFileChooser()
        }

        salvarButton.setOnClickListener {
            salvarItem()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun salvarItem() {
        val endereco = enderecoEditText.text.toString().trim()
        val tipoTreino = tipoTreinoEditText.text.toString().trim()
        val dataTreino = dataTreinoEditText.text.toString().trim()
        val recordTreino = recordTreinoEditText.text.toString().trim()
        val obsTreino = obsTreinoEditText.text.toString().trim()

        if (endereco.isEmpty() || tipoTreino.isEmpty() || dataTreino.isEmpty() || recordTreino.isEmpty() || obsTreino.isEmpty() || imageUri == null) {
            Toast.makeText(context, "Por favor, preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        uploadImageToFirestore()
    }



    private fun uploadImageToFirestore() {
        if (imageUri != null) {
            val inputStream = context?.contentResolver?.openInputStream(imageUri!!)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes != null) {
                val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)

                val endereco = enderecoEditText.text.toString().trim()
                val tipoTreino = tipoTreinoEditText.text.toString().trim()
                val dataTreino = dataTreinoEditText.text.toString().trim()
                val recordTreino = recordTreinoEditText.text.toString().trim()
                val obsTreino = obsTreinoEditText.text.toString().trim()

                val item = Item( base64Image, endereco, tipoTreino, dataTreino, recordTreino, obsTreino)

                saveItemIntoDatabase(item)
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            imageUri = data.data
            Glide.with(this).load(imageUri).into(itemImageView)
        }
    }

    private fun saveItemIntoDatabase(item: Item) {
        //TODO("Altere a raiz que sera criada no seu banco de dados do realtime database.
        // Renomeie a raiz itens")
        databaseReference = FirebaseDatabase.getInstance().getReference("itens")

        // Cria uma chave unica para o novo item
        val itemId = databaseReference.push().key
        if (itemId != null) {
            databaseReference.child(auth.uid.toString()).child(itemId).setValue(item)
                .addOnSuccessListener {
                    Toast.makeText(context, "Treino cadastrado com sucesso!", Toast.LENGTH_SHORT)
                        .show()
                    requireActivity().supportFragmentManager.popBackStack()
                }.addOnFailureListener {
                    Toast.makeText(context, "Falha ao cadastrar o treino", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Erro ao gerar o ID do treino", Toast.LENGTH_SHORT).show()
        }
    }
}