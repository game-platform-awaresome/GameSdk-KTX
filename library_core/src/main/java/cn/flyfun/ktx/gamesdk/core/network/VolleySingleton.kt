package cn.flyfun.ktx.gamesdk.core.network

import android.content.Context
import cn.flyfun.support.volley.Request
import cn.flyfun.support.volley.RequestQueue
import cn.flyfun.support.volley.toolbox.Volley

/**
 * @author #Suyghur.
 * Created on 2020/12/2
 */
class VolleySingleton private constructor(context: Context) {

    companion object {
        @Volatile
        private var instance: VolleySingleton? = null

        fun getInstance(context: Context): VolleySingleton {
            if (instance == null) {
                synchronized(VolleySingleton::class) {
                    if (instance == null) {
                        instance = VolleySingleton(context)
                    }
                }
            }
            return instance!!
        }
    }

    private var requestQueue: RequestQueue? = null

    init {
        requestQueue = Volley.newRequestQueue(context)
    }

    fun <T> addToRequestQueue(request: Request<T>) {
        requestQueue?.apply {
            add(request)
        }
    }

}