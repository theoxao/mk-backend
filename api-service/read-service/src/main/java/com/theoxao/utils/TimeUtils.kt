package com.theoxao.utils

object TimeUtils {

    fun seconds2Time(seconds: Long?): String? {
        return seconds?.let {
            var temp: Long = 0
            val sb = StringBuffer()
            temp = seconds!! / 3600
            sb.append(if (temp < 10) "0$temp:" else "$temp:")

            temp = seconds % 3600 / 60
            sb.append(if (temp < 10) "0$temp:" else "$temp:")

            temp = seconds % 3600 % 60
            sb.append(if (temp < 10) "0$temp" else "" + temp)
            sb.toString()
        }
    }
}
