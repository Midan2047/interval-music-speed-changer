package com.ddodang.intervalmusicspeedchanger.presentation.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.StringRes
import com.ddodang.intervalmusicspeedchanger.presentation.databinding.DialogLoadingBinding

class LoadingDialog(context: Context) : Dialog(context) {

    private var _binding: DialogLoadingBinding? = null
    private val binding
        get() = _binding!!

    private var message = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DialogLoadingBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        binding.textViewLoading.text = message
    }

    fun show(message: String) {
        this.message = message
        show()
    }

    fun show(@StringRes messageResId: Int) {
        show(context.getString(messageResId))
    }
}