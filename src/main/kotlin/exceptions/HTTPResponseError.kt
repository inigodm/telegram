package exceptions

class HTTPResponseError(val code: Int) : Exception()
