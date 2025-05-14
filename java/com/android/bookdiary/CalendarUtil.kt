/*
어시슈트 - 소북소북 코드입니다.

정보보호학과 2020111323 김지원
정보보호학과 2021111325 김해린
정보보호학과 2021111336 송다은(팀 대표)
정보보호학과 2021111694 이가연

 */

package com.android.bookdiary

import android.os.Build
import java.time.LocalDate

class CalendarUtil {

    companion object{
        var selectedDate: LocalDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()//   로컬 컴퓨터의 현재 날짜 가져오기
        } else {
            TODO("VERSION.SDK_INT < O")
        }

    }
}