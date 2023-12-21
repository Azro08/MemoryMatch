package com.example.memorymatch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.memorymatch.databinding.FragmentEndGamePopupBinding

class EndGamePopupFragment : Fragment() {
    private var _binding: FragmentEndGamePopupBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEndGamePopupBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val coins = arguments?.getInt("coins") ?: 0
        binding.textViewCoins.text = coins.toString()
        binding.imageViewHome.setOnClickListener {
            findNavController().navigate(R.id.nav_end_start)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}