package jp.techacademy.yoshikazu.takahashi.apiapp

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_api.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.lang.StringBuilder

class ApiFragment: Fragment() {
    private val apiAdapter by lazy { ApiAdapter(requireContext()) }
    private val handler = Handler(Looper.getMainLooper())

    private var fragmentCallback: FragmentCallback? = null
    private var page = 0
    private var isLoading = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentCallback) {
            fragmentCallback = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_api, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiAdapter.apply {
            onClickAddFavorite = {
                fragmentCallback?.onAddFavorite(it)
            }
            onClickDeleteFavorite = {
                fragmentCallback?.onDeleteFavorite(it.id)
            }

            onClickItem = {
                fragmentCallback?.onClickItem(it)
            }
        }

        recycleView.apply {
            adapter = apiAdapter
            layoutManager = LinearLayoutManager(requireContext())

            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if(dy == 0) {
                        return
                    }
                    val totalCount = apiAdapter.itemCount
                    val lastVisibleItem = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if(!isLoading && lastVisibleItem >= totalCount - 6) {
                        updateData(true)
                    }
                }
            })
        }
        swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
        updateData()

    }

    fun updateView() {
        recycleView.adapter?.notifyDataSetChanged()
    }

    private fun updateData(isAdd: Boolean = false) {
        if (isLoading) {
            return
        } else {
            isLoading = true
        }
        if (isAdd) {
            page++
        } else {
            page = 0
        }
        val start = page * COUNT +1
        val url = StringBuilder()
            .append(getString(R.string.base_url))
            .append("?key=").append(getString(R.string.api_key))
            .append("&start=").append(start)
            .append("&count=").append(COUNT)
            .append("&keyword=").append(getString(R.string.api_keyword))
            .append("&format=json")
            .toString()
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level=HttpLoggingInterceptor.Level.BODY
            })
            .build()
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                handler.post{
                    updateRecycleView(listOf(), isAdd)
                }
                isLoading = false
            }

            override fun onResponse(call: Call, response: Response) {
                var list = listOf<Shop>()
                response.body?.string()?.also {
                    val apiResponse = Gson().fromJson(it, ApiResponse::class.java)
                    list = apiResponse.results.shop
                }
                handler.post {
                    updateRecycleView(list, isAdd)
                }
                isLoading = false
            }
        })
    }

    private fun updateRecycleView(list: List<Shop>, isAdd: Boolean) {
        if (isAdd) {
            apiAdapter.add(list)
        } else {
            apiAdapter.refresh(list)
        }
        swipeRefreshLayout.isRefreshing = false
    }

    companion object {
        private const val COUNT = 20
    }
}