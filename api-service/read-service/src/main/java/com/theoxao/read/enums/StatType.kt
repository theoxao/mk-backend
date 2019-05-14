package com.theoxao.read.enums

enum class StatType(val code: Int) {
    BY_DATE(0), BY_PERIOD(1), BY_BOOK(2);

    companion object {
        fun getType(type: Int): StatType {
            for (value in values()) {
                if (value.code == type)
                    return value
            }
            return StatType.BY_DATE;
        }
    }
}

enum class StatSource(val code: Int) {
    MINUTE(0), PAGE(1);

    companion object {
        fun getType(type: Int): StatSource {
            for (value in values()) {
                if (value.code == type)
                    return value
            }
            return StatSource.MINUTE;
        }
    }

}
