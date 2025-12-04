package com.ebc.crud_basico.ViewModels

import androidx.lifecycle.ViewModel
import com.ebc.crud_basico.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeekQuoteViewModel: ViewModel() {

    private val api = ApiClient.geekQuoteApi


}