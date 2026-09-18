package com.example.data.model

data class NetworkDevice(
    val ipAddress: String,
    val macAddress: String,
    val vendor: String,
    val hostname: String,
    val isAuthorized: Boolean,
    val isSelf: Boolean = false,
    val isGateway: Boolean = false,
    val lastSeenTimestamp: Long,
    val openPorts: List<Int> = emptyList(),
    val threatFlag: String? = null
)
