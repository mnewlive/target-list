package com.mandarine.targetList.features.targets.list

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.mandarine.targetList.R
import com.mandarine.targetList.common.SwipeToDeleteCallback
import com.mandarine.targetList.common.setVisible
import com.mandarine.targetList.interfaces.BaseDataSetContract
import com.mandarine.targetList.interfaces.ListItemClickListener
import com.mandarine.targetList.interfaces.SelectTargetViewContract
import com.mandarine.targetList.model.Target
import kotlinx.android.synthetic.main.fragment_target_list.*
import kotlinx.android.synthetic.main.list_item_targets.*

class TargetsFragment : Fragment(), ListItemClickListener, SelectTargetViewContract,
    BaseDataSetContract {

    private var recyclerView: RecyclerView? = null
    private val presenter = TargetsPresenter(this)
    private var adapter = TargetsAdapter(clickListener = this)
    private lateinit var swipeHandler: ItemTouchHelper.Callback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_target_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.setInitialData()
        setupViews()
        updateListData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        swipeHandler = object : SwipeToDeleteCallback(context) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                presenter.removeListItem(viewHolder.adapterPosition)
            }
        }
    }

    override fun onListItemClick(itemIndex: Int, itemCode: String) {
        Log.d("some", "onListItemClick")
        presenter.onListItemClick((adapter.getItem(itemIndex) as Target).guid)
        Log.d("some", "onListItemClick guid: ${(adapter.getItem(itemIndex) as Target).guid}")
    }

    override fun showTarget(guid: String) {
        Log.d("some", "showTarget")
        val bundle = Bundle()
        bundle.putString("guid", guid)
        Log.d("some", "bundle, $bundle")
        contentLayout?.setOnClickListener {
            findNavController().navigate(R.id.target_edit, bundle)
        }
    }

    override fun dataSetChanged() {
        updateListData()
        adapter.notifyDataSetChanged()
    }

    override fun updateViewContent() {
        adapter.data = presenter.targetList
        recyclerView?.adapter = adapter
        recyclerView?.setVisible(presenter.shouldShowContent())
        emptyView?.setVisible(presenter.shouldShowEmptyView())
    }

    private fun updateListData() {
        if (presenter.firebaseUser == null) {
            Log.d("some", "loadLogInView")
        } else {
            presenter.getTargetsFromDb()
        }
    }

    private fun setupViews() {
        Log.d("some", "setupViews")
        fab?.setOnClickListener {
            findNavController().navigate(R.id.target_edit, null)
        }
        recyclerView = view?.findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)
    }
}
