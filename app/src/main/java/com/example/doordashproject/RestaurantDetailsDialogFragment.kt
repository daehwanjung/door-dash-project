package com.example.doordashproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.example.core.RestaurantMetadataProvider
import com.example.core.common.ThreadManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class RestaurantDetailsDialogFragment : BottomSheetDialogFragment(), CoroutineScope {
    companion object {
        const val TAG = "RestaurantDetailsDialogFragment"
        const val ID_KEY = "ID"
    }

    private val metadataProvider: RestaurantMetadataProvider by inject()
    private val threadManager: ThreadManager by inject()

    override val coroutineContext = CoroutineName("details")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_restaurant_details, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup(arguments?.getInt(ID_KEY) ?: return)
    }

    private fun setup(id: Int) {
        launch {
            val metadata = metadataProvider.metadata(id)
            threadManager.runOnMainThread {
                view?.findViewById<TextView>(R.id.name)?.text = metadata.name
                view?.findViewById<TextView>(R.id.phone)?.text = metadata.phoneNumber
                view?.findViewById<TextView>(R.id.address)?.text = metadata.address
                view?.findViewById<TextView>(R.id.rating)?.text = metadata.rating.toString()
                view?.findViewById<ProgressBar>(R.id.dialog_progress_bar)?.visibility = View.GONE
            }
        }
    }
}