package com.example.mitiendaonline

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout

class BuscarBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var onQueryChanged: ((String) -> Unit)? = null

    init {
        orientation = HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.view_buscar_bar, this, true)

        val etBuscar = findViewById<EditText>(R.id.etBuscar)
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onQueryChanged?.invoke(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
