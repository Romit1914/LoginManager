package com.yogitechnolabs.loginmanager.saloonapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yogitechnolabs.loginmanager.saloonapp.data.Owner
import com.yogitechnolabs.loginmanager.saloonapp.data.SalonRepository

class SalonViewModel : ViewModel() {

    private val repo = SalonRepository()

    private val _ownerLive = MutableLiveData<Owner>()
    val ownerLive: LiveData<Owner> get() = _ownerLive

    fun loadOwner(ownerId: String) {
        _ownerLive.value = repo.getOwnerData(ownerId)
    }
}