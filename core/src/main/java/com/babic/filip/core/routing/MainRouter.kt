package com.babic.filip.core.routing

interface MainRouter {

    fun showTopRated()

    fun showUpcoming()

    fun showFeed()

    fun showMessages()

    fun showMyProfile()

    fun refreshPage()

    fun showGameDetails(gameId: String)
}