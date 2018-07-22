package com.babic.filip.core.base

import android.arch.lifecycle.ViewModel
import com.babic.filip.core.routing.Router
import com.babic.filip.core.routing.RoutingDispatcher
import com.filip.babic.data.coroutineContext.CoroutineContextProvider
import com.filip.babic.data.networking.error.ApiDataTransformationException
import com.filip.babic.data.networking.error.AuthenticationError
import com.filip.babic.data.networking.error.NetworkException
import com.filip.babic.data.networking.error.ServerError
import kotlinx.coroutines.experimental.channels.BroadcastChannel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlin.coroutines.experimental.CoroutineContext

abstract class BaseViewModel<Data : Any, View : BaseView>(private val contextProvider: CoroutineContextProvider) : ViewModel(), StateViewModel<Data, View> {

    private val bufferCapacity = 1

    protected lateinit var view: View

    override fun viewReady(view: View) {
        this.view = view
        checkInitialState()
        start()
    }

    abstract fun initialState(): Data

    private fun checkInitialState() {
        if (!::state.isInitialized) {
            pushViewState(initialState())
        }
    }

    fun onDestroy() {
        router = null
        stateChannel.close()
    }

    protected fun start() = Unit

    private lateinit var state: Data
    private val stateChannel by lazy { BroadcastChannel<Data>(bufferCapacity) }

    override fun viewState(): ReceiveChannel<Data> = stateChannel.openSubscription()

    val main: CoroutineContext by lazy { contextProvider.main }
    val background: CoroutineContext by lazy { contextProvider.io }

    protected fun withState(consumer: (Data) -> Unit) = consumer(state)

    protected fun pushViewState(viewState: Data) {
        this.state = viewState

        sendStateDownstream()
    }

    protected fun processError(throwable: Throwable?) = when (throwable) {
        is ServerError -> showServerError()
        is ApiDataTransformationException -> showDataParseError()
        is NetworkException -> showNetworkError()
        is AuthenticationError -> showAuthenticationError()
        else -> Unit
    }

    //override to provide network connection error logic
    open fun showNetworkError() = view.showNetworkError()

    //override to provide parsing error logic
    open fun showDataParseError() = view.showParseError()

    //override to provide server error logic
    open fun showServerError() = view.showServerError()

    //override to provide authentication error logic
    open fun showAuthenticationError() = view.showAuthenticationError()

    protected fun changeViewState(editor: (Data) -> Unit) {
        withState(editor)

        sendStateDownstream()
    }

    private fun sendStateDownstream() {
        if (!stateChannel.isClosedForSend) {
            stateChannel.offer(state)
        }
    }

    private var router: RoutingDispatcher<Router>? = null

    override fun setRoutingSource(routingDispatcher: RoutingDispatcher<Router>) {
        this.router = routingDispatcher
    }

    fun dispatchRoutingAction(action: (Router) -> Unit) {
        router?.dispatchRoutingAction(action)
    }
}