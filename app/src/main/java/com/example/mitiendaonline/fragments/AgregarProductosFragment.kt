package com.example.mitiendaonline.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts // Nuevo
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.mitiendaonline.R
import com.example.mitiendaonline.data.dao.daoProducto
import com.example.mitiendaonline.data.model.Producto
import com.google.android.material.button.MaterialButton // Importa MaterialButton
import com.google.android.material.imageview.ShapeableImageView // Importa ShapeableImageView
import com.google.android.material.textfield.TextInputEditText // Importa TextInputEditText
import com.google.android.material.textfield.TextInputLayout // Importa TextInputLayout
import java.io.File
import java.io.IOException
import java.text.NumberFormat // Importa NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class AgregarProductosFragment : Fragment() {

    // UI elements (actualizado para Material Design)
    private lateinit var tilProductName: TextInputLayout
    private lateinit var etName: TextInputEditText
    private lateinit var tilProductDescription: TextInputLayout
    private lateinit var etDescription: TextInputEditText
    private lateinit var tilProductPrice: TextInputLayout
    private lateinit var etPrice: TextInputEditText
    private lateinit var tilProductStock: TextInputLayout
    private lateinit var etStock: TextInputEditText
    private lateinit var ivImage: ShapeableImageView // Cambiado a ShapeableImageView
    private lateinit var btnSaveProduct: MaterialButton // Cambiado a MaterialButton
    private lateinit var btnSelectImage: MaterialButton // Cambiado a MaterialButton
    private lateinit var btnCancel: MaterialButton // Añadido el botón de cancelar

    // Image and permissions
    private var currentImageUri: Uri? = null // Renombrado para claridad
    private lateinit var currentPhotoPath: String // Para la ruta de la foto de la cámara

    // Uso de ActivityResultLauncher para permisos y resultados de actividad (API moderna)
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            takePhoto()
        } else {
            Toast.makeText(requireContext(), "Permiso de cámara denegado. No se puede tomar la foto.", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImageFromGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val originalUri = result.data?.data
            originalUri?.let { uri ->
                val copiedUri = copyUriToInternalStorage(uri)
                currentImageUri = copiedUri
                ivImage.setImageURI(currentImageUri)
                ivImage.scaleType = ImageView.ScaleType.CENTER_CROP // Ajustar scaleType para imagen real
            }
        }
    }

    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // imageUri ya debería contener la URI de la foto tomada
            ivImage.setImageURI(currentImageUri)
            ivImage.scaleType = ImageView.ScaleType.CENTER_CROP // Ajustar scaleType para imagen real
        } else {
            // Si la foto no se tomó o se canceló, limpiar la URI temporal y el placeholder
            currentImageUri = null
            ivImage.setImageResource(R.drawable.ic_image_placeholder)
            ivImage.scaleType = ImageView.ScaleType.CENTER_INSIDE // Volver a scaleType para placeholder
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_agregar_productos, container, false) // Asegúrate que el XML sea dialog_agregar_producto.xml
        setupUI(view)
        setupListeners()
        // Establece el placeholder inicial para la imagen
        ivImage.setImageResource(R.drawable.ic_image_placeholder)
        ivImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
        return view
    }

    private fun setupUI(view: View) {
        // Inicializar los TextInputLayout y TextInputEditText
        tilProductName = view.findViewById(R.id.tilProductName)
        etName = view.findViewById(R.id.etName)
        tilProductDescription = view.findViewById(R.id.tilProductDescription) // Nuevo ID
        etDescription = view.findViewById(R.id.etDescription)
        tilProductPrice = view.findViewById(R.id.tilProductPrice)
        etPrice = view.findViewById(R.id.etPrice)
        tilProductStock = view.findViewById(R.id.tilProductStock)
        etStock = view.findViewById(R.id.etStock)

        ivImage = view.findViewById(R.id.ivImage) // Sigue siendo ImageView, pero ahora ShapeableImageView
        btnSaveProduct = view.findViewById(R.id.btnSaveProduct) // MaterialButton
        btnSelectImage = view.findViewById(R.id.btnSelectImage) // MaterialButton
        btnCancel = view.findViewById(R.id.btnCancel) // Nuevo botón de cancelar
    }

    private fun setupListeners() {
        btnSelectImage.setOnClickListener { showImagePickerDialog() }

        btnSaveProduct.setOnClickListener {
            if (validateInputs()) { // Llamar a la función de validación
                saveProduct()
            }
        }

        btnCancel.setOnClickListener {
            // Cierra el fragmento o regresa a la pantalla anterior
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val nombre = etName.text.toString().trim()
        val descripcion = etDescription.text.toString().trim()
        val precioText = etPrice.text.toString().trim()
        val stockText = etStock.text.toString().trim()

        if (nombre.isEmpty()) {
            tilProductName.error = "El nombre es obligatorio"
            isValid = false
        } else {
            tilProductName.error = null
        }

        if (descripcion.isEmpty()) {
            tilProductDescription.error = "La descripción es obligatoria"
            isValid = false
        } else {
            tilProductDescription.error = null
        }

        val precio = precioText.toDoubleOrNull()
        if (precioText.isEmpty()) {
            tilProductPrice.error = "El precio es obligatorio"
            isValid = false
        } else if (precio == null || precio <= 0) {
            tilProductPrice.error = "Ingrese un precio válido (> 0)"
            isValid = false
        } else {
            tilProductPrice.error = null
        }

        val stock = stockText.toIntOrNull()
        if (stockText.isEmpty()) {
            tilProductStock.error = "El stock es obligatorio"
            isValid = false
        } else if (stock == null || stock < 0) {
            tilProductStock.error = "Ingrese un stock válido (>= 0)"
            isValid = false
        } else {
            tilProductStock.error = null
        }

        if (currentImageUri == null) {
            Toast.makeText(requireContext(), "Debe seleccionar una imagen para el producto", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun saveProduct() {
        val nombre = etName.text.toString().trim()
        val descripcion = etDescription.text.toString().trim()
        val precio = etPrice.text.toString().toDouble() // Ya validado como Double
        val stock = etStock.text.toString().toInt() // Ya validado como Int

        val producto = Producto(0, nombre, descripcion, precio, stock, currentImageUri?.toString())
        val dao = daoProducto(requireContext())
        val result = dao.insertar(producto)

        if (result > 0) {
            Toast.makeText(context, "Producto agregado exitosamente", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack() // Regresar al fragmento anterior
        } else {
            Toast.makeText(context, "Error al guardar el producto", Toast.LENGTH_SHORT).show()
        }
    }

    // Selector de imagen: galería o cámara
    private fun showImagePickerDialog() {
        val options = arrayOf("Elegir de galería", "Tomar foto")
        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona una opción")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> pickImageFromGallery()
                    1 -> requestCameraPermission() // Usar la nueva función de solicitud de permiso
                }
            }
            .show()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
        pickImageFromGalleryLauncher.launch(intent)
    }

    private fun requestCameraPermission() {
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun takePhoto() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            Toast.makeText(requireContext(), "Error al crear archivo de imagen", Toast.LENGTH_SHORT).show()
            null
        }

        photoFile?.also {
            val photoUri: Uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider", // Asegúrate de que el provider esté configurado en tu manifest
                it
            )
            currentImageUri = photoUri // Guarda la URI temporal aquí
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            takePhotoLauncher.launch(takePictureIntent)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath // Guarda la ruta absoluta del archivo temporal
        }
    }

    private fun copyUriToInternalStorage(uri: Uri): Uri? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = createImageFile() // Crea un nuevo archivo para la copia
            val outputStream = file.outputStream()

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al copiar imagen: ${e.message}", Toast.LENGTH_LONG).show()
            null
        }
    }
}