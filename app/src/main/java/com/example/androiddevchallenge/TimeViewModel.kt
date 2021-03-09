/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androiddevchallenge.model.Time

class TimeViewModel : ViewModel() {

    private val _timeLeft = MutableLiveData<Time>()

    val timeLeft = _timeLeft as LiveData<Time>

    var timeTotal = 0L

    init {
        _timeLeft.value = Time()
    }

    private var timer: CountDownTimer? = null

    fun startTimer() {
        timeTotal = _timeLeft.value!!.inMillis()
        timer?.cancel()
        timer = object : CountDownTimer(timeTotal, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.value = Time(millisUntilFinished)
            }

            override fun onFinish() {
                _timeLeft.value = Time()
                timeTotal = 0
            }
        }
        timer?.start()
        _timeLeft.value = Time(timeTotal)
    }

    fun stopTimer() {
        timer?.cancel()
        _timeLeft.value = Time()
        timeTotal = 0
    }

    fun addHours(h: Int) {
        _timeLeft.value = Time(
            timeLeft.value!!.add(h = h)
        )
    }

    fun addMinutes(m: Int) {
        _timeLeft.value = Time(
            timeLeft.value!!.add(m = m)
        )
    }

    fun addSeconds(s: Int) {
        _timeLeft.value = Time(
            timeLeft.value!!.add(s = s)
        )
    }
}
