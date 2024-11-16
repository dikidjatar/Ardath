package com.djatar.ardath.feature.domain.models

data class UpdateProfileRequest(private val user: User) {
    private var name: String = user.name
    private var bio: String = user.bio
    private var photoUrl: String? = user.photoUrl
    private var status: String = user.status
    private var lastSeen: Long = user.lastSeen

    fun setName(name: String) = apply { this.name = name }
    fun setBio(bio: String) = apply { this.bio = bio }
    fun setPhotoUrl(url: String) = apply { this.photoUrl = url }
    fun setStatus(status: String) = apply { this.status = status }
    fun setLastSeen(timestamp: Long) = apply { this.lastSeen = timestamp }

    fun build(): User {
        return user.copy(
            name = name,
            bio = bio,
            photoUrl = photoUrl,
            status = status,
            lastSeen = lastSeen
        ).trim()
    }
}