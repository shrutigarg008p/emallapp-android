package com.emall.net.fragment.other

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.adapter.SavedCardsAdapter
import com.emall.net.listeners.ItemClick
import com.emall.net.model.CardItemDetails
import kotlinx.android.synthetic.main.fragment_payment.*

class PaymentFragment : Fragment(), ItemClick {

    private lateinit var savedCardsAdapter: SavedCardsAdapter
    private val cardList = ArrayList<CardItemDetails>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedCardsAdapter = SavedCardsAdapter(cardList,this)
        saved_cards_recycler_view.setHasFixedSize(true)
        saved_cards_recycler_view.layoutManager = LinearLayoutManager(activity)
        saved_cards_recycler_view.adapter = savedCardsAdapter
        prepareCardsList()

        checkout_btn.setOnClickListener {
            activity
                ?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.container, ConfirmationFragment(), "fragment")
                ?.addToBackStack("cart")
                ?.commit()
        }
    }

    private fun prepareCardsList() {
        cardList.clear()
        var item = CardItemDetails("Claudia T. Reyes.","2342 22**  ****  **00","06/23")
        cardList.add(item)
        item = CardItemDetails("Claudia T. Reyes.","2342 22**  ****  **00","06/23")
        cardList.add(item)
        savedCardsAdapter.notifyDataSetChanged()
    }

    override fun itemClick(count: Int) {
        TODO("Not yet implemented")
    }
}