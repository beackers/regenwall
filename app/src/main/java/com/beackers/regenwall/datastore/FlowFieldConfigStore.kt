package com.beackers.regenwall.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

fun readFlowFieldConfig(
    store: DataStore<FlowFieldConfigProto>
): FlowFieldConfigProto = runBlocking {
    store.data.first()
}

fun writeFlowFieldConfig(
    store: DataStore<FlowFieldConfigProto>,
    config: FlowFieldConfigProto
) = runBlocking {
    store.updateData { config }
}

