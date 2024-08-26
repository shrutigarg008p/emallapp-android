package com.emall.net.fragment.other

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emall.net.R
import kotlinx.android.synthetic.main.confirmation_page.*

class ConfirmationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.confirmation_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        place_order_btn.setOnClickListener {
            activity
                ?.supportFragmentManager?.beginTransaction()?.replace(R.id.container,OrderPlaceFragment(),"fragment")
                ?.addToBackStack("order")?.commit()
        }
    }
}