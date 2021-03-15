package com.example.doordashproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.core.RestaurantsPresenter
import com.example.core.RestaurantsView
import com.example.core.image.ImageLoader
import com.example.doordashproject.di.RestaurantsComponent
import com.example.doordashproject.list.RestaurantsAdapter
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf

class RestaurantsActivity : AppCompatActivity(), RestaurantsView {
    private val presenter: RestaurantsPresenter by inject()
    private val imageLoader: ImageLoader<ImageView> by inject { parametersOf(this) }

    private val swipeRefresh by lazy { findViewById<SwipeRefreshLayout>(R.id.swipe_refresh) }
    private val results by lazy { findViewById<RecyclerView>(R.id.results) }
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progress_bar) }
    private val empty by lazy { findViewById<TextView>(R.id.empty) }
    private val error by lazy { findViewById<TextView>(R.id.error) }
    private val adapter by lazy { RestaurantsAdapter(presenter, imageLoader) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(this@RestaurantsActivity)
            modules(RestaurantsComponent.module)
        }

        title = getString(R.string.discover)
        setContentView(R.layout.activity_restaurants)
        setAdapter()
        swipeRefresh.isRefreshing = true
        presenter.loadResults()
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }

    override fun onPause() {
        presenter.detachView()
        super.onPause()
    }

    private fun setAdapter() {
        swipeRefresh.setOnRefreshListener {
            presenter.loadResults()
        }

        val layoutManager = LinearLayoutManager(this)
        results.layoutManager = layoutManager
        results.adapter = adapter
        results.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        results.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && layoutManager.findLastCompletelyVisibleItemPosition() == presenter.itemCount - 1) {
                    progressBar.visibility = View.VISIBLE
                    swipeRefresh.isRefreshing = false
                    presenter.loadMoreResults()
                }
            }
        })
    }

    override fun updateResults() {
        swipeRefresh.isRefreshing = false
        progressBar.visibility = View.GONE
        empty.visibility = View.GONE
        error.visibility = View.GONE
        adapter.notifyDataSetChanged()
    }

    override fun updateMoreResults() {
        progressBar.visibility = View.GONE
        adapter.notifyDataSetChanged()
    }

    override fun handleResultsEmpty() {
        swipeRefresh.isRefreshing = false
        progressBar.visibility = View.GONE
        empty.visibility = View.VISIBLE
        empty.visibility = View.GONE
    }

    override fun handleMoreResultsEmpty() {
        progressBar.visibility = View.GONE
        showToast(R.string.load_more_empty)
    }

    override fun handleError() {
        swipeRefresh.isRefreshing = false
        progressBar.visibility = View.GONE
        empty.visibility = View.GONE
        error.visibility = View.VISIBLE
    }

    override fun showDetails(id: Int) {
        RestaurantDetailsDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(RestaurantDetailsDialogFragment.ID_KEY, id)
            }
        }.show(supportFragmentManager, RestaurantDetailsDialogFragment.TAG)
    }

    private fun showToast(stringId: Int) {
        Toast.makeText(this, getString(stringId), Toast.LENGTH_SHORT).show()
    }
}