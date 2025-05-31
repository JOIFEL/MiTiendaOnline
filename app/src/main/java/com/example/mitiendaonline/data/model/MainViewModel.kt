
package com.example.mitiendaonline.data.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val ubicacionActual = MutableLiveData<Coordenadas?>()
}