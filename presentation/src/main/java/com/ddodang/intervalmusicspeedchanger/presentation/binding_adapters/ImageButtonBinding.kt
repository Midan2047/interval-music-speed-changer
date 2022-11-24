package com.ddodang.intervalmusicspeedchanger.presentation.binding_adapters

import android.graphics.drawable.Drawable
import android.widget.ImageButton
import androidx.databinding.BindingAdapter

@BindingAdapter(value = ["nomi:onImage", "nomi:offImage", "nomi:on"], requireAll = true)
fun setImageSourceSelector(view: ImageButton, onImage: Drawable, offImage: Drawable, isOn: Boolean) {
    if (isOn) {
        view.setImageDrawable(onImage)
    } else {
        view.setImageDrawable(offImage)
    }
}