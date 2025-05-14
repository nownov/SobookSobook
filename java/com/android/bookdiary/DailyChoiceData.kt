/*
어시슈트 - 소북소북 코드입니다.

정보보호학과 2020111323 김지원
정보보호학과 2021111325 김해린
정보보호학과 2021111336 송다은(팀 대표)
정보보호학과 2021111694 이가연

 */

package com.android.bookdiary


// DailyChoice 프래그먼트에서 사용할 데이터
data class DailyChoiceData (

    val bookColor : String,
    val bookTitle : String,
    val date : String,
    val totalPage : Int,
    val accumPage : Int
)