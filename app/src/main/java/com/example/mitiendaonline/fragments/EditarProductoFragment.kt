package com.example.mitiendaonline.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.mitiendaonline.R
import com.example.mitiendaonline.data.dao.daoProducto
import com.example.mitiendaonline.data.model.Producto
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditarProductoFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etDescription: EditText
    private lateinit var etPrice: EditText
    private lateinit var etStock: EditText
    private lateinit var ivImage: ImageView
    private lateinit var btnSaveProduct: Button
    private lateinit var btnSelectImage: Button

    private var id: Int = 0
    private var nombre: String = ""
    private var descripcion: String = ""
    private var precio: Double = 0.0
    private var stock: Int = 0
    private var imagenUriString: String? = null


    private var imageUri: Uri? = null
    private val REQUEST_IMAGE_GALLERY = 1
    private val REQUEST_IMAGE_CAMERA = 2
    private val REQUEST_CAMERA_PERMISSION = 100



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt("id", 0)
            nombre = it.getString("nombre", "") ?: ""
            descripcion = it.getString("descripcion", "") ?: ""
            precio = it.getDouble("precio", 0.0)
            stock = it.getInt("stock", 0)
            imagenUriString = it.getString("imagenUri")
            imageUri = imagenUriString?.let { str -> Uri.parse(str) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_agregar_productos, container, false)

        etName = view.findViewById(R.id.etName)
        etDescription = view.findViewById(R.id.etDescription)
        etPrice = view.findViewById(R.id.etPrice)
        etStock = view.findViewById(R.id.etStock)
        ivImage = view.findViewById(R.id.ivImage)
        btnSaveProduct = view.findViewById(R.id.btnSaveProduct)
        btnSelectImage = view.findViewById(R.id.btnSelectImage)

        // Rellenar campos con datos recibidos
        etName.setText(nombre)
        etDescription.setText(descripcion)
        etPrice.setText(precio.toString())
        etStock.setText(stock.toString())
        if (imagenUriString != null) {
            ivImage.setImageURI(Uri.parse(imagenUriString))
        }

        btnSelectImage.setOnClickListener {
            showImagePickerDialog()
        }

        btnSaveProduct.setOnClickListener {
            val nombreEditado = etName.text.toString()
            val descripcionEditada = etDescription.text.toString()
            val precioEditado = etPrice.text.toString().toDoubleOrNull() ?: 0.0
            val stockEditado = etStock.text.toString().toIntOrNull() ?: 0

            val productoEditado = Producto(
                id = id,
                nombre = nombreEditado,
                descripcion = descripcionEditada,
                precio = precioEditado,
                stock = stockEditado,
                imagenUri = imageUri?.toString()
            )
            val result = daoProducto(requireContext()).actualizar(productoEditado)
            if (result > 0) {
                Toast.makeText(context, "Producto editado", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(context, "Error al editar", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Elegir de galería", "Tomar foto")
        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona una opción")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> pickImageFromGallery()
                    1 -> checkCameraPermissionAndTakePhoto()
                }
            }
            .show()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    private fun checkCameraPermissionAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            takePhoto()
        }
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }

            photoFile?.let {
                val uri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    it
                )
                imageUri = uri
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(intent, REQUEST_IMAGE_CAMERA)
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(null)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_GALLERY -> {
                    val originalUri = data?.data
                    val copiedUri = originalUri?.let { copyUriToInternalStorage(it) }
                    imageUri = copiedUri
                    ivImage.setImageURI(imageUri)
                }

                REQUEST_IMAGE_CAMERA -> {
                    ivImage.setImageURI(imageUri)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto()
            } else {
                Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun copyUriToInternalStorage(uri: Uri): Uri? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = createImageFile()
            val outputStream = file.outputStream()

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
