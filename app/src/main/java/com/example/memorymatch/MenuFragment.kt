package com.example.memorymatch

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.memorymatch.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {
    private var _binding : FragmentMenuBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(layoutInflater)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonPlay.setOnClickListener {
            findNavController().navigate(R.id.nav_menu_game_scene)
        }

        binding.imageViewPrivacy.setOnClickListener {
            //TODO
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}