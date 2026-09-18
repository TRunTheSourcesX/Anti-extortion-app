package com.example.data.model

data class MaliciousSignature(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val severity: ThreatLevel,
    val targetVector: String,
    val forensicTactics: String
)
