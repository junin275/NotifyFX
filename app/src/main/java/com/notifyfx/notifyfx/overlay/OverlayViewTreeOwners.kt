package com.notifyfx.notifyfx.overlay

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner

class OverlayViewTreeOwners : LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val viewModelStore = ViewModelStore()
    private val savedStateRegistryController = androidx.savedstate.SavedStateRegistryController.create(this)

    fun resume() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        savedStateRegistryController.performRestore(null)
    }

    fun pause() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    fun destroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry
    override fun getViewModelStore(): ViewModelStore = viewModelStore
    override fun getSavedStateRegistry(): SavedStateRegistry = savedStateRegistryController.savedStateRegistry
}