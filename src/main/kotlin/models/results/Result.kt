package models.results

sealed class Result<T> {
    open fun get():T?=null
    fun getOrThrow():T= when(this){
        is Success->get()
        is ErrorResult -> throw throwable
    }
}
data class Success<T>(val data:T) : Result<T>(){
    override fun get(): T {
        return data
    }
}
data class ErrorResult<T>(val throwable: Throwable):Result<T> ()

