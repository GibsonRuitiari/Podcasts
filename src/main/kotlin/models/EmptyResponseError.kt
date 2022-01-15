package models

class EmptyResponseError(override val message:String): Exception(message)