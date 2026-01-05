package com.beackers.regenwall.datastore

import androidx.datastore.core.Serializer
import androidx.datastore.core.CorruptionException
import java.io.InputStream
import java.io.OutputStream

object FlowFieldConfigSerializer : Serializer<FlowFieldConfigProto> {
    override val defaultValue: FlowFieldConfigProto = FlowFieldConfigProto.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): FlowFieldConfigProto {
        try {
            return FlowFieldConfigProto.parseFrom(input)
        } catch (e: Exception) {
            throw CorruptionException("Cannot read FlowFieldConfig", e)
        }
    }
    override suspend fun writeTo(
        t: FlowFieldConfigProto,
        output: OutputStream
    ) {
        t.writeTo(output)
    }
}
