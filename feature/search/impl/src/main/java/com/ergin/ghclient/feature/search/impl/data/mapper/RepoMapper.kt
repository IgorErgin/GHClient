package com.ergin.ghclient.feature.search.impl.data.mapper

import com.ergin.ghclient.core.database.entity.RepoEntity
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoId
import com.ergin.ghclient.feature.search.domain.model.Repo
import com.ergin.ghclient.feature.search.impl.data.remote.model.RepoDto

fun RepoDto.toDomain(): Repo {
    return Repo(
        id = RepoId(id),
        name = name,
        description = description,
        language = language,
        stars = stargazersCount,
        ownerName = OwnerName(owner.login),
        ownerAvatarUrl = owner.avatarUrl
    )
}

fun RepoDto.toEntity(isFavorite: Boolean = false): RepoEntity {
    return RepoEntity(
        id = id,
        name = name,
        ownerName = owner.login,
        ownerAvatarUrl = owner.avatarUrl,
        description = description,
        language = language,
        stars = stargazersCount,
        isFavorite = isFavorite
    )
}

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
