package com.ergin.ghclient.core.ui.components

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut

/**
 * Анимации переключения экранов в стиле кинескопа CRT (лампового монитора).
 * Имитируют вспышку фосфора и расширение кадра развертки при переключении каналов ЭВМ.
 */
object CrtNavTransitions {

    val enterTransition: EnterTransition = fadeIn(
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
    ) + scaleIn(
        initialScale = 0.92f,
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
    )

    val exitTransition: ExitTransition = fadeOut(
        animationSpec = tween(durationMillis = 280, easing = LinearOutSlowInEasing)
    ) + scaleOut(
        targetScale = 1.05f,
        animationSpec = tween(durationMillis = 280, easing = LinearOutSlowInEasing)
    )

    val popEnterTransition: EnterTransition = fadeIn(
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
    ) + scaleIn(
        initialScale = 1.05f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
    )

    val popExitTransition: ExitTransition = fadeOut(
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
    ) + scaleOut(
        targetScale = 0.92f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
    )
}
