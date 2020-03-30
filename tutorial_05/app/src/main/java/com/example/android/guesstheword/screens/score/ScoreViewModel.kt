package com.example.android.guesstheword.screens.score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreViewModel(finalScore: Int) : ViewModel() {
    private val _eventPlayAgain = MutableLiveData<Boolean>()
    val eventPlayAgain: LiveData<Boolean>
        get() = _eventPlayAgain

    // final score
    var score = finalScore
    init{
        Log.i("ScoreViewModel", "Final score is $finalScore")
    }

    fun onPlayAgain(){
        _eventPlayAgain.value = true
    }

    fun onPlayaAgainComplete(){
        _eventPlayAgain.value = false
    }

}