package com.yogitechnolabs.loginmanager.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.yogitechnolabs.loginmanager.databinding.FileUploaderViewBinding

class FileUploaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var binding: FileUploaderViewBinding =
        FileUploaderViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var onFileSelected: ((Uri, String) -> Unit)? = null
    private var allowedTypes: Array<String> = arrayOf("*/*")

    fun setup(
        activity: FragmentActivity,
        allowedTypes: List<String> = listOf("*/*"),
        onFileSelected: (Uri, String) -> Unit
    ) {
        this.allowedTypes = allowedTypes.toTypedArray()
        this.onFileSelected = onFileSelected

        val filePicker = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    val name = getFileName(activity, it)
                    binding.tvFileName.text = name
                    onFileSelected.invoke(it, name ?: "Unknown")
                }
            }
        }

        binding.btnUpload.setOnClickListener {
            val pickerIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = if (allowedTypes.size == 1) allowedTypes[0] else "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, allowedTypes.toTypedArray())
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            filePicker.launch(pickerIntent)
        }
    }

    private fun getFileName(activity: Activity, uri: Uri): String? {
        val cursor = activity.contentResolver.query(uri, null, null, null, null)
        val nameIndex = cursor?.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        cursor?.moveToFirst()
        val name = nameIndex?.let { cursor.getString(it) }
        cursor?.close()
        return name
    }
}
