package com.ergin.ghclient.feature.favorites.impl.data.mapper

import com.ergin.ghclient.core.database.entity.RepoEntity
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoId
import com.ergin.ghclient.feature.search.domain.model.Repo

fun RepoEntity.toDomain(): Repo {
    return Repo(
        id = RepoId(id),
        name = name,
        description = description,
        language = language,
        stars = stars,
        ownerName = OwnerName(ownerName),
        ownerAvatarUrl = ownerAvatarUrl
    )
}

fun Repo.toEntity(): RepoEntity {
    return RepoEntity(
        id = id.value,
        name = name,
        ownerName = ownerName.value,
        ownerAvatarUrl = ownerAvatarUrl,
        description = description,
        language = language,
        stars = stars,
        isFavorite = true
    )
}
