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
package com.example.androiddevchallenge.model

data class Time(
    private var millis: Long = 0L
) {
    private var hours: Int = 0
    private var minutes: Int = 0
    private var seconds: Int = 0

    init {
        val s = millis / 1000L
        hours = ((s / (60 * 60)) % 24).toInt()
        minutes = ((s / 60) % 60).toInt()
        seconds = (s % 60).toInt()
    }

    fun inMillis(): Long {
        val hh = hours * 60 * 60 * 1000L
        val mm = minutes * 60 * 1000L
        val ss = seconds * 1000L
        return hh + mm + ss
    }

    fun getHours() = hours

    fun getMinutes() = minutes

    fun getSeconds() = seconds

    fun add(h: Int = 0, m: Int = 0, s: Int = 0): Long {
        hours += h
        minutes += m
        seconds += s
        validate()
        return inMillis()
    }

    private fun validate() {
        when {
            hours < 0 -> hours = 0
            minutes !in 0..59 -> minutes = 0
            seconds !in 0..59 -> seconds = 0
        }
    }
}
