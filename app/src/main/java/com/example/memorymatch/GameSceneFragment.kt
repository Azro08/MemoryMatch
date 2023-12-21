package com.example.memorymatch

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.memorymatch.databinding.FragmentGameSceneBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameSceneFragment : Fragment() {
    private var _binding: FragmentGameSceneBinding? = null
    private val binding get() = _binding!!
    private var indexOfSingleSelectedCard: Int? = null
    private var cards: List<MemoryCard> = listOf()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var isDelayActive = false
    private var startTime: Long = 0L
    private var currentCoins = 100
    private var maxReward = 100
    private var minReward = 10
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameSceneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.textViewCoins.text = currentCoins.toString()
        val images = mutableListOf(
            R.drawable.boat_icon,
            R.drawable.car_icon,
            R.drawable.train_icon,
            R.drawable.ic_heart,
            R.drawable.ic_smiley,
            R.drawable.ic_light,
            R.drawable.plane_icon,
            R.drawable.phone_icon,
            R.drawable.ic_code,
            R.drawable.basketball_icon
        )
        images.addAll(images)
        images.shuffle()

        val buttons = getButtonList()

        cards = buttons.indices.map { index ->
            MemoryCard(images[index])
        }

        startTime = System.currentTimeMillis()
        startTimer()
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                Log.i("Button", "button clicked!!")
                // Update models
                updateModels(index)
                // Update the UI for the game
                updateViews(buttons)
            }
        }


    }

    private fun startTimer() {
        coroutineScope.launch {
            while (true) {
                delay(1000) // Update the timer every second
                updateTimer()
            }
        }
    }

    private fun updateTimer() {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = (currentTime - startTime) / 1000 // Calculate elapsed time in seconds

        // Calculate reward based on elapsed time
        currentCoins = if (elapsedTime <= 20) {
            maxReward // Maximum reward of 100 coins if completed in less than or equal to 20 seconds
        } else {
            val deductedCoins = (elapsedTime - 20) * 5
            val remainingCoins = maxReward - deductedCoins.toInt()

            // Ensure the remaining coins don't fall below the minimum reward
            maxOf(minReward, remainingCoins)
        }

        binding.textViewTimer.text = String.format("%02d:%02d", elapsedTime / 60, elapsedTime % 60)
        binding.textViewCoins.text = currentCoins.toString()
    }

    private fun getButtonList(): List<ImageButton> = with(binding) {
        return listOf(
            imageButton1,
            imageButton2,
            imageButton3,
            imageButton4,
            imageButton5,
            imageButton6,
            imageButton7,
            imageButton8,
            imageButton9,
            imageButton10,
            imageButton11,
            imageButton12,
            imageButton13,
            imageButton14,
            imageButton15,
            imageButton16,
            imageButton17,
            imageButton18,
            imageButton19,
            imageButton20
        )
    }

    private fun areAllPairsMatched(): Boolean {
        return cards.all { it.isMatched }
    }

    private fun updateViews(buttons: List<ImageButton>) {
        cards.forEachIndexed { index, card ->
            val button = buttons[index]
            if (card.isMatched) {
                button.alpha = 0.1f
            }
            button.setImageResource(if (card.isFaceUp) card.identifier else R.drawable.rounded_edges_background)
        }
    }

    private fun updateModels(position: Int) {
        val card = cards[position]
        // Error checking:
        if (card.isFaceUp) {
            Toast.makeText(requireContext(), "Invalid move!", Toast.LENGTH_SHORT).show()
            return
        }
        // Three cases
        // 0 cards previously flipped over => restore cards + flip over the selected card
        // 1 card previously flipped over => flip over the selected card + check if the images match
        // 2 cards previously flipped over => restore cards + flip over the selected card
        indexOfSingleSelectedCard = if (indexOfSingleSelectedCard == null) {
            // 0 or 2 selected cards previously
            restoreCards()
            position
        } else {
            // exactly 1 card was selected previously
            checkForMatch(indexOfSingleSelectedCard!!, position)
            if (!card.isMatched) {
                startDelay(indexOfSingleSelectedCard!!, position)
            }
            null
        }
        card.isFaceUp = !card.isFaceUp
    }

    private fun startDelay(position1: Int, position2: Int) {
        isDelayActive = true
        coroutineScope.launch {
            delay(1000) // 2 seconds delay
            if (!cards[position1].isMatched && !cards[position2].isMatched) {
                cards[position1].isFaceUp = false
                cards[position2].isFaceUp = false
                updateViews(getButtonList())
            }
            isDelayActive = false
        }
    }

    private fun restoreCards() {
        for (card in cards) {
            if (!card.isMatched) {
                card.isFaceUp = false
            }
        }
    }

    private fun checkForMatch(position1: Int, position2: Int) {
        if (cards[position1].identifier == cards[position2].identifier) {
            cards[position1].isMatched = true
            cards[position2].isMatched = true
            if (areAllPairsMatched()) {
                endGameAndShowResult()
            }
        }
    }

    private fun endGameAndShowResult() {
        val currentTime = System.currentTimeMillis()
        val elapsedTime =
            (currentTime - startTime) / 1000 // Calculate total elapsed time in seconds

        currentCoins = if (elapsedTime <= 20) {
            maxReward // Maximum reward of 100 coins if completed in less than or equal to 20 seconds
        } else {
            val deductedCoins = (elapsedTime - 20) * 5
            val remainingCoins = maxReward - deductedCoins.toInt()
            maxOf(
                minReward,
                remainingCoins
            ) // Ensure the remaining coins don't fall below the minimum reward
        }

        val message =
            "Congratulations! You've completed the game in $elapsedTime seconds. You earned $currentCoins coins!"
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        Log.d("Coins", currentCoins.toString())
        findNavController().navigate(R.id.nav_game_end, bundleOf(Pair("coins", currentCoins)))
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}