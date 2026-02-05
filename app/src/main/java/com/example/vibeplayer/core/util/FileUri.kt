package com.example.vibeplayer.core.util

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import java.io.File

//avoid hardcoding jpg cause picked image might be webp, png and other types
private fun getFileExtension(context: Context, uri: Uri): String {
    val mimeType = context.contentResolver.getType(uri)
    return MimeTypeMap.getSingleton()
        .getExtensionFromMimeType(mimeType) ?: "jpg"
}

//save the image inside an internal storage to avoid losing it if image deleted from gallery
//fileId is the playlist id since playlist name can contain spaces , . * and more unsupported characters
fun copyImageToInternalStorage(
    context: Context,
    uri: Uri,
    fileId: Int
): Uri {
    //delete if exist and old cover
    deleteOldCover(context = context, fileId = fileId)

    val extension = getFileExtension(context, uri)
    //same uri can have different content which lead to room not update the new cover change
    //so we add timestamp to have a different uri for the same playlist
    val timestamp = System.currentTimeMillis()
    val file = File(context.filesDir, "cover_$fileId.$timestamp.$extension")

    context.contentResolver.openInputStream(uri)?.use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return file.toUri()
}

//delete any existing cover with this file id
fun deleteOldCover(
    context: Context,
    fileId: Int
) {
    context.filesDir.listFiles { file ->
        file.name.startsWith("cover_$fileId.")
    }?.forEach { file ->
        if (file.exists()) file.delete()
    }
}
