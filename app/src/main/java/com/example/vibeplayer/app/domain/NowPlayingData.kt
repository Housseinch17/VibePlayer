package com.example.vibeplayer.app.domain

import android.net.Uri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
sealed interface NowPlayingData {
    @Serializable(with = PlayByUriSerializer::class)
    data class PlayByUri(val uri: Uri?) : NowPlayingData

    @Serializable
    data object Play : NowPlayingData

    @Serializable
    data object Shuffle : NowPlayingData
}

object PlayByUriSerializer : KSerializer<NowPlayingData.PlayByUri> {
    override val descriptor = PrimitiveSerialDescriptor("PlayByUri", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: NowPlayingData.PlayByUri) {
        encoder.encodeString(value.uri?.toString() ?: "")
    }

    override fun deserialize(decoder: Decoder): NowPlayingData.PlayByUri {
        val uriString = decoder.decodeString()
        return NowPlayingData.PlayByUri(uriString.takeIf { it.isNotEmpty() }?.let { Uri.parse(it) })
    }
}
