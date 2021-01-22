//package cn.flyfun.gamesdk.core.network
//
//import cn.flyfun.gamesdk.base.utils.Logger
//import cn.flyfun.support.volley.NetworkResponse
//import cn.flyfun.support.volley.ParseError
//import cn.flyfun.support.volley.Request
//import cn.flyfun.support.volley.Response
//
//
///**
// * @author #Suyghur,
// * Created on 2021/1/22
// */
//class FileRequest : Request<ByteArray> {
//
//    private var mListener: Response.Listener<ByteArray>? = null
//
//
//    constructor(url: String, listener: Response.Listener<ByteArray>, errorListener: Response.ErrorListener) : super(Method.GET, url, errorListener) {
//        mListener = listener
//    }
//
//    constructor(requestMethod: Int, url: String, listener: Response.Listener<ByteArray>, errorListener: Response.ErrorListener) : super(requestMethod, url, errorListener) {
//        mListener = listener
//    }
//
//    override fun parseNetworkResponse(networkResponse: NetworkResponse?): Response<ByteArray> {
//        synchronized(sDecodeLock) {
//            try {
//
//            } catch (e: OutOfMemoryError) {
//                Logger.e("caught oom , url $url")
//                return Response.error(ParseError(e))
//            }
//        }
//    }
//
//    override fun deliverResponse(p0: ByteArray?) {
//        TODO("Not yet implemented")
//    }
//
//    companion object {
//        private val sDecodeLock: Any = Any()
//    }
//}